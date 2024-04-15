package com.arnacon.chat_library

import android.content.ContentValues
import android.database.Cursor
import java.time.Instant
import java.util.UUID

data class ChatSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val sessionName: String,
    val type: String,
    val participants: MutableList<String>, // Changed to MutableList for direct modification
    val createdAt: Long = Instant.now().toEpochMilli(),
    var lastMessage: Long = createdAt
) {
    class Builder {
        private var sessionId: String = UUID.randomUUID().toString()
        private lateinit var sessionName: String
        private lateinit var type: String
        private lateinit var participants: MutableList<String>
        private var createdAt: Long = Instant.now().toEpochMilli()
        private var lastMessage: Long = createdAt

        fun sessionId(sessionId: String) = apply { this.sessionId = sessionId }
        fun sessionName(sessionName: String) = apply { this.sessionName = sessionName }
        fun type(type: String) = apply { this.type = type }
        fun participants(participants: MutableList<String>) = apply { this.participants = participants }

        fun build(): ChatSession {
            return ChatSession(sessionId, sessionName, type, participants, createdAt, lastMessage)
        }
    }

    fun addParticipant(participantId: String) {
        if (type != "group") {
            throw IllegalArgumentException("Cannot add participant to a non-group session.")
        } else if (participants.contains(participantId)) {
            throw IllegalArgumentException("User already in group.")
        } else {
            participants.add(participantId)
        }
    }

    fun removeParticipant(participantId: String) {
        (participants as? MutableList)?.remove(participantId) // Caution: Original list must be mutable.
    }

    fun updateLastMessageTimestamp(timestamp: Long) {
        lastMessage = timestamp
    }

    fun serialize(): ContentValues {
        return ContentValues().apply {
            put("sessionId", sessionId)
            put("sessionName", sessionName)
            put("type", type)
            put("participants", participants.joinToString(","))
            put("createdAt", createdAt)
            put("lastMessage", lastMessage)
        }
    }

    companion object {
        fun fromCursor(cursor: Cursor): ChatSession {
            val participantsString = cursor.getString(cursor.getColumnIndexOrThrow("participants"))
            val participants = participantsString.split(",").toMutableList()
            return ChatSession(
                cursor.getString(cursor.getColumnIndexOrThrow("sessionId")),
                cursor.getString(cursor.getColumnIndexOrThrow("sessionName")),
                cursor.getString(cursor.getColumnIndexOrThrow("type")),
                participants,
                cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")),
                cursor.getLong(cursor.getColumnIndexOrThrow("lastMessage"))
            )
        }

        fun getStructure(): Map<String, String> {
            return mapOf(
                "sessionId" to "TEXT",
                "sessionName" to "TEXT",
                "type" to "INTEGER",
                "participants" to "TEXT",
                "createdAt" to "TEXT",
                "lastMessage" to "TEXT"
            )
        }
    }
}