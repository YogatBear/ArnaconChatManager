package com.arnacon.chat_library

import android.content.Context
import org.json.JSONObject
import java.io.File
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val chatIndex = Index(context)
    private val pubSub = PubSub(context, user)
    private val fileManager = FileManager("/storage/emulated/0/Download", user)

    init {
        pubSub.listenForNewMessages { message ->
            // Handle the new message here
            onNewMessageReceived(message)
        }
    }

    fun sendTextMessage(text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = JSONObject().apply {
            put("sender", user) // Assuming 'user' is a class property with the sender's name
            put("text", text)
        }
        val newMessage = Message(messageId, "text", contentJson.toString())
        chatIndex.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)
    }

    suspend fun sendFileMessage(file: File, text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = fileManager.UploadFile(messageId, file)
        val newMessage = Message(messageId, "file", contentJson.toString())
        chatIndex.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)
    }

    private fun onNewMessageReceived(newMessage: Message) {
        chatIndex.storeMessage(newMessage)
    }

    fun getRecentMessages(count: Int = 10): List<Message> {
        // Retrieve the most recent 'count' messages
        return chatIndex.getMessages(0, count)
    }

    // Additional functionalities can be added here
}
