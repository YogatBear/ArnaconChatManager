package com.arnacon.chat_library

import java.util.UUID
import java.time.Instant

class Message {
    var messageId: String = ""
    var sender: String = ""
    var timestamp: Long = 0L
    var type: String = ""
    var content: String = ""

    // No-argument constructor for Firebase
    constructor()
    // Constructor for creating a new message
    constructor(sender: String, type: String, content: String) {
        this.messageId = UUID.randomUUID().toString()
        this.sender = sender
        this.timestamp = Instant.now().toEpochMilli()
        this.type = type
        this.content = content
    }

    // Constructor for received message
    constructor(messageId: String, sender: String, timestamp: Long, type: String, content: String) {
        this.messageId = messageId
        this.sender = sender
        this.timestamp = timestamp
        this.type = type
        this.content = content
    }

    companion object {
        fun getStructure(): Map<String, String> {
            return mapOf(
                "messageId" to "TEXT",
                "sender" to "TEXT",
                "timestamp" to "INTEGER",
                "type" to "TEXT",
                "content" to "TEXT"
            )
        }
    }
}
