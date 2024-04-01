package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatManager(context: Context, private val user: String) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val pubSub: PubSub = Firestore(user)
    private val storage = Storage(context)
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

    fun newMessage(messageType: String, content: String, uri: Uri?, onSuccess: (Message) -> Unit, onError: (Throwable) -> Unit) {
        val messageBuilder = Message.Builder().type(messageType)
        scope.launch {
            when (messageType) {
                "text" -> {
                    val contentJson = metadata.textMetadata(user, content)
                    val message = messageBuilder.content(contentJson).build()
                    onSuccess(message)
                }

                "file" -> {
                    try {
                        val messageId = messageBuilder.getMessageId() // Assuming getMessageId() is static or appropriately instantiated
                        val fileMetadata = storage.uploadFile(user, messageId, uri.toString())
                        val message = messageBuilder.content(fileMetadata).build()
                        withContext(Dispatchers.Main) { onSuccess(message) }
                    } catch (e: Throwable) {
                        withContext(Dispatchers.Main) { onError(e) }
                    }
                }

                else -> onError(IllegalArgumentException("Invalid message type: $messageType"))
            }
        }
    }


    fun storeMessage(message: Message) {
        storage.storeMessage(message)

        displayMessage(message)
    }

    fun uploadMessage(message: Message) {
        pubSub.uploadMessage(message)
    }

    private fun onNewMessageReceived(newMessage: Message) {
        scope.launch {
            storage.storeMessage(newMessage)
            if (newMessage.type == "file") {
                storage.downloadFile(newMessage.messageId, newMessage.content)
            }
            displayMessage(newMessage)
        }
    }

    fun loadRecentMessages(start: Int = 0, count: Int = 10) {
        val recentMessages = storage.getMessages(start, count)
        val displayedMessages = recentMessages.map { DisplayedMessage.fromMessage(it, storage) }
        updateListener?.onNewMessages(displayedMessages)
    }

    fun displayMessage(message: Message){
        val displayedMessage = DisplayedMessage.fromMessage(message, storage)
        updateListener?.onNewMessage(displayedMessage)
    }
}
