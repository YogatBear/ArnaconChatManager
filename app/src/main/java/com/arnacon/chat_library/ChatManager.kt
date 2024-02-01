package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class ChatManager(private val context: Context, private val user: String) {
    private val pubSub: PubSub = Firestore(context, user)
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

    suspend fun NewMessage(messageType: String, content: String, uri: Uri? = null): Message {
        val messageBuilder = Message.Builder().type(messageType)

        val contentJson = when (messageType) {
            "text" -> metadata.textMetadata(user, content)
            "file" -> {
                uri?.let {
                    val messageId = messageBuilder.getMessageId()
                    storage.uploadFile(user, messageId, it.toString())
                } ?: throw IllegalArgumentException("URI must be provided for file messages.")
            }
            else -> throw IllegalArgumentException("Invalid message type: $messageType")
        }

        return messageBuilder.content(contentJson).build()
    }

    fun StoreMessage(message: Message) {
        storage.storeMessage(message)

        DisplayMessage(message)
    }

    fun UploadMessage(message: Message) {
        pubSub.uploadMessage(message)
    }

    private suspend fun onNewMessageReceived(newMessage: Message) {
        storage.storeMessage(newMessage)
        if (newMessage.type == "file") {
            storage.downloadFile(newMessage.messageId, newMessage.content)
        }
        DisplayMessage(newMessage)
    }

    fun loadRecentMessages(start: Int = 0, count: Int = 10) {
        val recentMessages = storage.getMessages(start, count)
        val displayedMessages = recentMessages.map { DisplayedMessage.fromMessage(it, storage) }
        updateListener?.onNewMessages(displayedMessages)
    }

    private fun DisplayMessage(message: Message){
        val displayedMessage = DisplayedMessage.fromMessage(message, storage)
        updateListener?.onNewMessage(displayedMessage)
    }
}
