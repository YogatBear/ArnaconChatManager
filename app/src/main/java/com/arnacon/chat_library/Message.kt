package com.arnacon.chat_library

import android.content.ContentValues
import android.database.Cursor
import java.time.Instant
import java.util.UUID

class Message private constructor(
    val messageId: String,
    val timestamp: Long,
    val type: String,
    val content: String
) {

    class Builder {
        private var messageId: String = UUID.randomUUID().toString()
        private var timestamp: Long = Instant.now().toEpochMilli()
        private lateinit var type: String
        private lateinit var content: String

        fun type(type: String) = apply { this.type = type }
        fun content(content: String) = apply { this.content = content }
        fun getMessageId(): String = messageId

        fun build(): Message {
            return Message(messageId, timestamp, type, content)
        }
    }

    fun serialize(): ContentValues {
        return ContentValues().apply {
            put("messageId", messageId)
            put("timestamp", timestamp)
            put("type", type)
            put("content", content)
        }
    }

    companion object {
        fun fromCursor(cursor: Cursor): Message {
            return Message(
                cursor.getString(cursor.getColumnIndexOrThrow("messageId")),
                cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                cursor.getString(cursor.getColumnIndexOrThrow("type")),
                cursor.getString(cursor.getColumnIndexOrThrow("content"))
            )
        }

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
