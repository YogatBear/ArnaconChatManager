package com.arnacon.chat_library

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class Pinata() {
    private val pinataApiKey = ""
    private val pinataSecretApiKey = ""

    private val client = OkHttpClient()

    suspend fun uploadToPinata(file: File): String {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name,
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
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

            return json.getString("IpfsHash")
        }
    }
}
