package com.arnacon.chat_library

import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class IPFS : FileSharingStrategy {

    private val client = OkHttpClient()
    private val pinata = Pinata()

    override suspend fun uploadFile(file: File): String {
        return pinata.uploadToPinata(file)
    }

    override suspend fun downloadFile(fileId: String, destinationFile: File) {
        val url = URL("https://ipfs.io/ipfs/$fileId") // Replace with your IPFS gateway if needed
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val inputStream = response.body?.byteStream() ?: throw IOException("Response body is null")
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
