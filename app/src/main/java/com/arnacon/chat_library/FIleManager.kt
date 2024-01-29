package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FileManager(private val context: Context) {

    private val pinataApiKey = "83becdb068502664cb04"
    private val pinataSecretApiKey = "f8b1977450922eb286b8d77464d68b8d91485dc951687f18a951513cc410b5f8"
    private val pinataService = Pinata(pinataApiKey, pinataSecretApiKey)
    private val ipfsService = IPFS()
    private val metadata = Metadata()

    private fun getAppSpecificExternalDir(): File {
        val directory = File(context.getExternalFilesDir(null), "ChatFiles")
        if (!directory.exists()) {
            val created = directory.mkdirs()
            if (!created) {
                Log.e("FileManager", "Failed to create directory: ${directory.absolutePath}")
            } else {
                Log.d("FileManager", "Directory created: ${directory.absolutePath}")
            }
        }
        return directory
    }

    suspend fun uploadFile(user: String, messageId: String, uriString: String): JSONObject? {
        val fileUri = uriString.toUri()
        Log.d("FileManager", "Uploading file for messageId: $messageId")
        try {
            val fileName = getFileNameFromUri(fileUri)
            val fileExtension = getFileExtension(fileName)
            val destinationFile = File(getAppSpecificExternalDir(), "$messageId.$fileExtension")

            context.contentResolver.openInputStream(fileUri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            val cid = pinataService.uploadToPinata(destinationFile)
            return metadata.fileMetadata(user, destinationFile, fileName, cid)
        } catch (e: Exception) {
            Log.e("FileManager", "Error uploading file: ${e.message}")
            return null
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var filename = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            filename = cursor.getString(nameIndex)
        }
        return filename
    }

    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }

    suspend fun downloadFile(messageId: String, cid: String): File? {
        try {
            val destinationFile = File(getAppSpecificExternalDir(), messageId)
            ipfsService.downloadFromIPFS(cid, destinationFile)
            return destinationFile
        } catch (e: IOException) {
            Log.e("FileManager", "Error downloading file: ${e.message}")
            return null
        }
    }

    fun getFileUri(messageId: String): Uri? {
        try {
            val downloadFolder = getAppSpecificExternalDir()
            val files = downloadFolder.listFiles { _, name -> name.startsWith(messageId) }
            val file = files?.firstOrNull()

            if (file != null && file.exists()) {
                val contentUri = FileProvider.getUriForFile(context, "com.arnacon.chat_library.provider", file)
                Log.d("FileManager", "Generated Content URI: $contentUri")
                return contentUri
            } else {
                Log.e("FileManager", "File not found or doesn't exist for message ID: $messageId")
                return null
            }
        } catch (e: IllegalArgumentException) {
            Log.e("FileManager", "Error getting file URI: ${e.message}")
            return null
        }
    }
}
