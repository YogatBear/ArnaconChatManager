package com.arnacon.chat_library

interface PubSub {
    fun uploadMessage(message: Message)
    fun listenForNewMessages(onNewMessageReceived: (Message) -> Unit)
}