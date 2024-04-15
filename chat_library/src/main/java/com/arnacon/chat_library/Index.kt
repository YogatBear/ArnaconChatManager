package com.arnacon.chat_library

interface Index {
    fun storeMessage(message: Message, sessionId: String)
    fun getMessages(start: Int = 0, range: Int = 10, sessionId: String): List<Message>
    fun sessionExists(sessionId: String): Boolean
    fun storeSession(session: ChatSession)
    fun updateSession(session: ChatSession)
    fun getSession(sessionId: String): ChatSession?
    fun getSessions(): List<ChatSession>
}