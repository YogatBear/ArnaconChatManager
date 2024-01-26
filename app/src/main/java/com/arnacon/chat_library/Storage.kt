package com.arnacon.chat_library

import android.content.Context
import org.json.JSONObject
import java.io.File
import java.util.UUID

class Storage(private val context: Context,
              private val downloadFolderPath: String) {
    private val chatIndex = Index(context)
    private val fileManager = FileManager(downloadFolderPath)

    fun storeMessage(message: Message) {
        chatIndex.storeMessage(message)
    }

    fun getMessages(start: Int, count: Int): List<Message> {
        // Retrieve the most recent 'count' messages
        return chatIndex.getMessages(start, count)
    }

    suspend fun uploadFile(user: String, messageId: String, file: File): String {
        val contentJson = fileManager.UploadFile(user, messageId, file)
        return contentJson.toString()
    }
}