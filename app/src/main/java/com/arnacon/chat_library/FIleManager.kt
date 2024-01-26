package com.arnacon.chat_library

import org.json.JSONObject
import java.io.File

class FileManager(private val downloadFolderPath: String) {

    private val pinataApiKey = "83becdb068502664cb04"
    private val pinataSecretApiKey = "f8b1977450922eb286b8d77464d68b8d91485dc951687f18a951513cc410b5f8"
    private val pinataService = Pinata(pinataApiKey, pinataSecretApiKey)
    private val ipfsService = IPFS()
    private val metadata = Metadata()

    suspend fun UploadFile(user: String, messageId: String, file: File): JSONObject {
        val destinationFile = File(downloadFolderPath, "$messageId.${(file.extension)}")
        file.copyTo(destinationFile, overwrite = true)
        val cid = pinataService.uploadToPinata(destinationFile)
        return metadata.fileMetadata(user, destinationFile, file.name, cid)
    }

    // Method to download a file from IPFS
    suspend fun downloadFile(filename: String, cid: String): File {
        val destinationFilePath = "$downloadFolderPath/$filename"
        val destinationFile = File(destinationFilePath)
        ipfsService.downloadFromIPFS(cid, destinationFile)
        return destinationFile
    }
}
