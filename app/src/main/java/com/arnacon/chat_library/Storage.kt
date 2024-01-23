package com.arnacon.chat_library

import android.content.Context
import org.json.JSONObject
import java.io.File
import java.util.UUID

class Storage(private val context: Context,
              private val user: String,
              private val downloadFolderPath: String) {
    private val chatIndex = Index(context)
    private val fileManager = FileManager(downloadFolderPath, user)

    fun storeMessage(message: Message) {
        chatIndex.storeMessage(message)
    }

    fun getMessages(start: Int, count: Int): List<Message> {
        // Retrieve the most recent 'count' messages
        return chatIndex.getMessages(start, count)
    }

    fun textMetadata(text: String): String {
        val contentJson = JSONObject().apply {
            put("sender", user)
            put("text", text)
        }
        return contentJson.toString()
    }

    suspend fun fileMetadata(messageId: String, file: File): String {
        val contentJson = fileManager.UploadFile(messageId, file)
        return contentJson.toString()
    }
}