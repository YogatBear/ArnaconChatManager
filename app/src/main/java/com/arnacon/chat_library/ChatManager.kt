package com.arnacon.chat_library

import android.content.Context
import java.io.File
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val chatIndex = Index(context)
    private val pubSub = PubSub(context, user)
    private val fileManager = FileManager("/storage/emulated/0/Download")

    init {
        pubSub.listenForNewMessages { message ->
            // Handle the new message here
            onNewMessageReceived(message)
        }
    }

    fun sendTextMessage(text: String) {
        val messageId = UUID.randomUUID().toString()
        // Create a new Message object using its constructor for new messages
        val newMessage = Message(messageId, user, "text", text)
        // Store the message in the database
        chatIndex.storeMessage(newMessage)
        // Upload the message to firestore
        pubSub.uploadMessage(newMessage)
    }

    suspend fun sendFileMessage(file: File, text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = fileManager.UploadFile(messageId, file)
        val newMessage = Message(messageId, user, "file", contentJson.toString())
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
