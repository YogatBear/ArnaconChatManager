/*package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatManagerJava(private val context: Context, private val user: String) {
    private val chatManager: ChatManager = ChatManager(context, user)

    fun setUpdateListener(listener: ChatManager.ChatUpdateListener) {
        chatManager.updateListener = listener
    }

    fun loadRecentMessages(start: Int = 0, count: Int = 10) {
        chatManager.loadRecentMessages(start, count)
    }

    fun newMessage(messageType: String, content: String, uri: Uri?, onSuccess: (Message) -> Unit, onError: (Throwable) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val message = chatManager.newMessage(messageType, content, uri)
                onSuccess(message)
            } catch (e: Throwable) {
                onError(e)
            }
        }
    }

    fun storeMessage(message: Message) {
        chatManager.storeMessage(message)
    }

    fun uploadMessage(message: Message) {
        chatManager.uploadMessage(message)
    }
}*/