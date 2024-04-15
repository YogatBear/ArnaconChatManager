package com.arnacon.chat_library

import android.content.Context

class SessionManager(context: Context) {

    private val storage = Storage(context)
    fun newSession(context: String){
        val sessionBuilder = ChatSession.Builder()
        val session = sessionBuilder
            .sessionId(context)
            .sessionName(context)
            .participants(mutableListOf(context))
            .type("private")
            .build()
        storage.storeSession(session)
    }

    fun getSession(context: String): ChatSession?{
        return storage.getSession(context)
    }

    fun loadSessions(): List<ChatSession>{
        return storage.getSessions()
    }

    fun updateSession(context: String, timestamp: Long) {
        val session = getSession(context)
        if (session != null) {
            session.updateLastMessageTimestamp(timestamp)
            storage.updateSession(session)
        }
    }

}