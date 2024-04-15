package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import org.json.JSONObject

class Storage(context: Context) {

    private val index: Index = SQLiteIndex(DatabaseHelper(context))
    private val fileManager = FileManager(context,GCS())

    fun storeMessage(message: Message, sessionId: String) {
        index.storeMessage(message, sessionId)
    }

    fun getMessages(start: Int, count: Int, sessionId: String): List<Message> {
        return index.getMessages(start, count, sessionId)
    }

    fun storeSession(session: ChatSession) {
        index.storeSession(session)
    }

    fun updateSession(session: ChatSession) {
        index.updateSession(session)
    }

    fun getSession(sessionId: String): ChatSession? {
        return index.getSession(sessionId)
    }

    fun getSessions(): List<ChatSession> {
        return index.getSessions()
    }

    suspend fun uploadFile(user: String, messageId: String, uri: String): String {
        val contentJson = fileManager.uploadFile(user, messageId, uri)
        return contentJson.toString()
    }

    suspend fun downloadFile(messageId: String, contentJson: String) {
        val jsonObject = JSONObject(contentJson)
        val cid = jsonObject.getString("cid")
        fileManager.downloadFile(messageId, cid)
    }

    fun fileExists(messageId: String): Boolean{
        return fileManager.fileExists(messageId)
    }

    fun getFileUri(messageId: String): Uri? {
        return fileManager.getFileUri(messageId)
    }

    fun deleteDatabase(context:Context) {
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME)
    }
}