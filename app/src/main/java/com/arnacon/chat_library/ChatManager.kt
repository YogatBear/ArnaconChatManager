package com.arnacon.chat_library

import android.content.Context
import java.io.File
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val pubSub = PubSub(context, user)
    private val storage = Storage(context, "/storage/emulated/0/Download")
    private val metadata = Metadata()
    var updateListener: ChatUpdateListener? = null

    init {
        pubSub.listenForNewMessages { message ->
            onNewMessageReceived(message)
        }
    }

    interface ChatUpdateListener {
        fun onNewMessage(displayedMessage: DisplayedMessage)
        fun onNewMessages(displayedMessages: List<DisplayedMessage>)
    }

    fun sendTextMessage(text: String) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = metadata.textMetadata(user, text)
        val newMessage = Message(messageId, "text", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)

        val displayedMessage = DisplayedMessage.fromMessage(newMessage)
        updateListener?.onNewMessage(displayedMessage)
    }


    suspend fun sendFileMessage(text: String, file: File) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = storage.uploadFile(user, messageId, file)
        val newMessage = Message(messageId, "file", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)
    }

    private fun onNewMessageReceived(newMessage: Message) {
        storage.storeMessage(newMessage)
        val displayedMessage = DisplayedMessage.fromMessage(newMessage)
        updateListener?.onNewMessage(displayedMessage)
    }

    fun loadRecentMessages(start: Int = 0, count: Int = 10) {
        val recentMessages = storage.getMessages(start, count)
        val displayedMessages = recentMessages.map(DisplayedMessage::fromMessage)
        updateListener?.onNewMessages(displayedMessages)
    }
}
