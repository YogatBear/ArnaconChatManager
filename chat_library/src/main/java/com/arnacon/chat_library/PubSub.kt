package com.arnacon.chat_library

interface PubSub {
    fun uploadMessage(message: Message,recipient: String)
    fun listenForNewMessages(onNewMessageReceived: (Message) -> Unit)
}