package com.arnacon.chat_library

import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class IPFS {

    private val client = OkHttpClient()

    suspend fun downloadFromIPFS(ipfsHash: String, downloadLocation: File) {
        val url = URL("https://ipfs.io/ipfs/$ipfsHash") // Replace with your IPFS gateway if needed
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val inputStream = response.body?.byteStream() ?: throw IOException("Response body is null")
            FileOutputStream(downloadLocation).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
