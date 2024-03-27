package com.arnacon.chat_library

import androidx.core.net.toUri
import java.io.File
import java.io.FileInputStream
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.security.MessageDigest

class GCS() : FileSharingStrategy {

    private val storage = Firebase.storage("gs://dulcet-clock-403610.appspot.com")
    private val storageRef = storage.reference


    override suspend fun uploadFile(file: File): String {
        val fileHash = hashFile(file)
        val fileRef = storageRef.child(fileHash)

        try {
            // Start the file upload
            fileRef.putFile(file.toUri()).await()

            // After upload is complete, get the download URL
            val downloadUri = fileRef.downloadUrl.await()
            return downloadUri.toString()
        } catch (e: Exception) {
            // Handle the error, e.g., log it or throw it
            throw e
        }
    }

    override suspend fun downloadFile(fileId: String, destinationFile: File) {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(fileId)
                .build()

            // Execute the request
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Failed to download file: $response")

                // Save the downloaded file to the specified destination
                response.body?.byteStream()?.use { inputStream ->
                    destinationFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: throw IOException("Empty response body")
            }
        }
    }

    private fun hashFile(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        FileInputStream(file).use { fis ->
            var count: Int
            while (fis.read(buffer).also { count = it } > 0) {
                digest.update(buffer, 0, count)
            }
        }
        return bytesToHex(digest.digest())
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789abcdef".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}
