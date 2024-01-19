package com.arnacon.chat_library

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class IPFS(private val pinataApiKey: String, private val pinataSecretApiKey: String) {

    private val client = OkHttpClient()

    suspend fun uploadToIPFS(file: File): Map<String, String> {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file))
            .build()

        val request = Request.Builder()
            .url("https://api.pinata.cloud/pinning/pinFileToIPFS")
            .post(requestBody)
            .addHeader("pinata_api_key", pinataApiKey)
            .addHeader("pinata_secret_api_key", pinataSecretApiKey)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string() ?: throw IOException("Response body is null")
            val json = JSONObject(responseBody)
            val fileExtension = file.name.substringAfterLast('.', "")

            return mapOf(
                "type" to fileExtension,
                "CID" to json.getString("IpfsHash"),
                "filename" to file.name
            )
        }
    }

    suspend fun downloadFromIPFS(ipfsHash: String, downloadLocation: File): File {
        val url = URL("https://ipfs.io/ipfs/$ipfsHash") // Replace with your IPFS gateway if needed
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val inputStream = response.body?.byteStream() ?: throw IOException("Response body is null")
            FileOutputStream(downloadLocation).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return downloadLocation
    }
}
