package com.arnacon.chat_library

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.IOException

class Pinata() {
    private val pinataApiKey = "83becdb068502664cb04"
    private val pinataSecretApiKey = "f8b1977450922eb286b8d77464d68b8d91485dc951687f18a951513cc410b5f8"

    private val client = OkHttpClient()

    suspend fun uploadToPinata(file: File): String {
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

            return json.getString("IpfsHash")
        }
    }
}
