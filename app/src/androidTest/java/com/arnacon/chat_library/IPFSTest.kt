package com.arnacon.chat_library

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class IPFSTest {

    private val pinataApiKey = "83becdb068502664cb04"
    private val pinataSecretApiKey = "f8b1977450922eb286b8d77464d68b8d91485dc951687f18a951513cc410b5f8"
    private val ipfsService = IPFS(pinataApiKey, pinataSecretApiKey)

    @Test
    fun testUploadAndDownload() {
        runBlocking {
            // Create a temporary file with some content
            val tempFile = File.createTempFile("test", ".txt")
            tempFile.writeText("Hello IPFS")

            // Upload the file
            val uploadResponse = ipfsService.uploadToIPFS(tempFile)
            val cid = uploadResponse["CID"] ?: throw AssertionError("CID not found")

            // Download the file
            val downloadLocation = File(tempFile.parent, "downloaded_${tempFile.name}")
            ipfsService.downloadFromIPFS(cid, downloadLocation)

            // Verify the content
            val originalContent = tempFile.readText()
            val downloadedContent = downloadLocation.readText()
            assertEquals(originalContent, downloadedContent)

            // Clean up
            tempFile.delete()
            downloadLocation.delete()
        }
    }
}
