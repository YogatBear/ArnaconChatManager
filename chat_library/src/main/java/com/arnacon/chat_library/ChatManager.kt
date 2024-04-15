package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class ChatManager(context: Context, private val user: String) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val pubSub: PubSub = FirestoreMessaging(user)
    private val storage = Storage(context)
    private val sessionManager = SessionManager(context)
    private val metadata = Metadata()

    fun newMessage(messageType: String, content: String, uri: Uri?, context: String): CompletableFuture<Message> {
        val futureMessage = CompletableFuture<Message>()
        val messageBuilder = Message.Builder().type(messageType).context(context)
        scope.launch {
            when (messageType) {
                "text" -> {
                    val textMetadata = metadata.textMetadata(user, content)
                    val message = messageBuilder.content(textMetadata).build()
                    futureMessage.complete(message)
                }

                "file" -> {
                    try {
                        val messageId = messageBuilder.getMessageId() // Assuming getMessageId() is accessible here
                        val fileMetadata = storage.uploadFile(user, messageId, uri.toString())
                        val message = messageBuilder.content(fileMetadata).build()
                        futureMessage.complete(message)
                    } catch (e: Throwable) {
                        futureMessage.completeExceptionally(e)
                    }
                }

                else -> futureMessage.completeExceptionally(IllegalArgumentException("Invalid message type: $messageType"))
            }
        }
        return futureMessage
    }


    fun storeMessage(message: Message, sessionId: String): CompletableFuture<Void> {
        storage.storeMessage(message, sessionId)
        sessionManager.getSession(sessionId) ?: sessionManager.newSession(sessionId)
        sessionManager.updateSession(sessionId, message.timestamp)
        return if (message.type == "file" && !storage.fileExists(message.messageId)) {
            val future = CompletableFuture<Void>()
            scope.launch {
                try {
                    storage.downloadFile(message.messageId, message.content)
                    future.complete(null) // Successfully completed the download
                } catch (e: Throwable) {
                    future.completeExceptionally(e) // Failed to download
                }
            }
            future
        } else {
            CompletableFuture.completedFuture(null) // Return a completed future when no download is needed
        }
    }

    fun uploadMessage(message: Message, recipient: String) {
        pubSub.uploadMessage(message, recipient)
    }

    fun loadRecentMessages(start: Int = 0, count: Int = 10, sessionId: String): List<Message> {
        val recentMessages = storage.getMessages(start, count, sessionId)
        return recentMessages.map { it }
    }

    fun getDisplayedMessage(message: Message): DisplayedMessage {
        return DisplayedMessage.fromMessage(message, storage)
    }
}
