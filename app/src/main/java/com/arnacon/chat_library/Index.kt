package com.arnacon.chat_library

interface Index {
    fun storeMessage(message: Message)
    fun getMessages(start: Int = 0, range: Int = 10): List<Message>
}