package com.arnacon.chat_library

import android.content.Context
import android.net.Uri
import org.json.JSONObject

class Storage(private val context: Context) {
    /*
    init {
        // Delete the existing database
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME)
    }
     */
    private val index: Index = SQLiteIndex(DatabaseHelper(context))
    private val fileManager = FileManager(context,GCS())

    fun storeMessage(message: Message) {
        index.storeMessage(message)
    }

    fun getMessages(start: Int, count: Int): List<Message> {
        return index.getMessages(start, count)
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

    fun getFileUri(messageId: String): Uri? {
        return fileManager.getFileUri(messageId)
    }
}