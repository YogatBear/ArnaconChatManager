package com.arnacon.chat_library

import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

class FileManager(private val downloadFolderPath: String) {

    private val pinataApiKey = "83becdb068502664cb04"
    private val pinataSecretApiKey = "f8b1977450922eb286b8d77464d68b8d91485dc951687f18a951513cc410b5f8"
    private val pinataService = Pinata(pinataApiKey, pinataSecretApiKey)
    private val ipfsService = IPFS()

    // Method to store a file (upload to IPFS via Pinata)
    suspend fun UploadFile(file: File): JSONObject {
        val destinationFile = File(downloadFolderPath, "${UUID.randomUUID()}.$(file.extension)")
        file.copyTo(destinationFile, overwrite = true)
        val cid = pinataService.uploadToPinata(destinationFile)
        return createMetadata(destinationFile, file.name, cid)
    }

    // Method to download a file from IPFS
    suspend fun downloadFile(filename: String, cid: String): File {
        val destinationFilePath = "$downloadFolderPath/$filename"
        val destinationFile = File(destinationFilePath)
        ipfsService.downloadFromIPFS(cid, destinationFile)
        return destinationFile
    }


    // Method to create metadata for a file
    fun createMetadata(file: File, filename: String,  cid: String): JSONObject {
        return JSONObject().apply {
            put("filename", filename)
            put("filetype", file.extension)
            put("filesize", file.length())
            put("cid", cid)  // CID of the file
        }
    }
}
