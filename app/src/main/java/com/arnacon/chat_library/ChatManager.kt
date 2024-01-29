package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val pubSub = PubSub(context, user)
    private val storage = Storage(context)
    private val metadata = Metadata()
    var updateListener: ChatUpdateListener? = null

    init {
        pubSub.listenForNewMessages { message ->
            GlobalScope.launch {
                onNewMessageReceived(message)
            }
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

        val displayedMessage = DisplayedMessage.fromMessage(newMessage, storage)
        updateListener?.onNewMessage(displayedMessage)
    }


    suspend fun sendFileMessage(text: String, uri: Uri) {
        val messageId = UUID.randomUUID().toString()
        val contentJson = storage.uploadFile(user, messageId, uri.toString())
        val newMessage = Message(messageId, "file", contentJson)

        storage.storeMessage(newMessage)
        pubSub.uploadMessage(newMessage)

        val displayedMessage = DisplayedMessage.fromMessage(newMessage, storage)
        updateListener?.onNewMessage(displayedMessage)
    }

    private suspend fun onNewMessageReceived(newMessage: Message) {
        storage.storeMessage(newMessage)
        if (newMessage.type == "file") {
            storage.downloadFile(newMessage.messageId, newMessage.content)
        }
        val displayedMessage = DisplayedMessage.fromMessage(newMessage, storage)
        updateListener?.onNewMessage(displayedMessage)
    }

    fun loadRecentMessages(start: Int = 0, count: Int = 10) {
        val recentMessages = storage.getMessages(start, count)
        val displayedMessages = recentMessages.map { DisplayedMessage.fromMessage(it, storage) }
        updateListener?.onNewMessages(displayedMessages)
    }
}
