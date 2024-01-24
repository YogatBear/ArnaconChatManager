package com.arnacon.chat_library

import android.content.Context
import java.io.File
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val pubSub = PubSub(context, user)
    private val storage = Storage(context, user, "/storage/emulated/0/Download")
    var updateListener: ChatUpdateListener? = null

    init {
        pubSub.listenForNewMessages { message ->
            onNewMessageReceived(message)
        }
    }

    interface ChatUpdateListener {
        fun onNewMessage(displayedMessage: DisplayedMessage)
    }

    fun sendTextMessage(text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = storage.textMetadata(text)
        val newMessage = Message(messageId, "text", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)

        val displayedMessage = DisplayedMessage.fromMessage(newMessage)
        updateListener?.onNewMessage(displayedMessage)
    }

    suspend fun sendFileMessage(text: String, file: File) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = storage.fileMetadata(messageId, file)
        val newMessage = Message(messageId, "file", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)
    }

    private fun onNewMessageReceived(newMessage: Message) {
        storage.storeMessage(newMessage)
        val displayedMessage = DisplayedMessage.fromMessage(newMessage)
        updateListener?.onNewMessage(displayedMessage)
    }

    fun getRecentMessages(start: Int = 0, count: Int = 10): List<Message> {
        // Retrieve the most recent 'count' messages
        return storage.getMessages(start, count)
    }
    // Additional functionalities...
}
