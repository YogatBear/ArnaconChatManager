package com.arnacon.chat_library

import android.content.Context
import java.io.File
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val pubSub = PubSub(context, user)
    private val storage = Storage(context, user, "/storage/emulated/0/Download")

    init {
        pubSub.listenForNewMessages { message ->
            // Handle the new message here
            onNewMessageReceived(message)
        }
    }

    fun sendTextMessage(text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = storage.textMetadata(text)
        val newMessage = Message(messageId, "text", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)
    }

    suspend fun sendFileMessage(file: File, text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = storage.fileMetadata(messageId, file)
        val newMessage = Message(messageId, "file", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)
    }

    fun getRecentMessages(start: Int = 0, count: Int = 10): List<Message> {
        // Retrieve the most recent 'count' messages
        return storage.getMessages(start, count)
    }

    private fun onNewMessageReceived(newMessage: Message) {
        storage.storeMessage(newMessage)
    }

    // Additional functionalities can be added here
}
