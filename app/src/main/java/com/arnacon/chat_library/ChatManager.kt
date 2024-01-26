package com.arnacon.chat_library

import android.content.Context
import android.util.Log
import java.io.File
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val pubSub = PubSub(context, user)
    private val storage = Storage(context, user, "/storage/emulated/0/Download")
    var updateListener: ChatUpdateListener? = null

    init {
        val recentMessages = getRecentMessages(0, 9)
        val displayedMessages = recentMessages.map { message ->
            DisplayedMessage.fromMessage(message)
        }
        updateListener?.onNewMessages(displayedMessages)

        pubSub.listenForNewMessages { message ->
            onNewMessageReceived(message)
        }
    }

    interface ChatUpdateListener {
        fun onNewMessage(displayedMessage: DisplayedMessage)
        fun onNewMessages(displayedMessages: List<DisplayedMessage>) // Add this method
    }

    fun sendTextMessage(text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = storage.textMetadata(text)
        val newMessage = Message(messageId, "text", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)

        val displayedMessage = DisplayedMessage.fromMessage(newMessage)

        updateListener?.onNewMessage(displayedMessage)

        Log.d("ChatRoomActivity", "$displayedMessage")
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
