package com.arnacon.chat_library

import android.database.Cursor
import java.time.Instant

class Message {

    var messageId: String = ""
    var timestamp: Long = 0L
    var type: String = ""
    var content: String = ""

    // No-argument constructor for Firebase
    constructor()

    // Constructor for creating a new message
    constructor(messageId: String, type: String, content: String) {
        this.messageId = messageId
        this.timestamp = Instant.now().toEpochMilli()
        this.type = type
        this.content = content
    }

    // Constructor for received message
    constructor(messageId: String, sender: String, timestamp: Long, type: String, content: String) {
        this.messageId = messageId
        this.timestamp = timestamp
        this.type = type
        this.content = content
    }

    // Constructor to build from a Cursor
    constructor(cursor: Cursor) {
        messageId = cursor.getString(cursor.getColumnIndexOrThrow("messageId"))
        timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
        type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
        content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
    }

    companion object {
        fun getStructure(): Map<String, String> {
            return mapOf(
                "messageId" to "TEXT",
                "timestamp" to "INTEGER",
                "type" to "TEXT",
                "content" to "TEXT"
            )
        }
    }
}
