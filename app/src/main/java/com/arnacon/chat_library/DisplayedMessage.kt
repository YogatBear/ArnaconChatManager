package com.arnacon.chat_library

import org.json.JSONObject

sealed class DisplayedMessage(open val messageId: String, open val sender: String, open val timestamp: Long) {
    data class TextMessage(
        override val messageId: String,
        override val sender: String,
        override val timestamp: Long,
        val text: String
    ) : DisplayedMessage(messageId, sender, timestamp)

    data class FileMessage(
        override val messageId: String,
        override val sender: String,
        override val timestamp: Long,
        val filename: String,
        val filetype: String,
        val filesize: Long,
        val cid: String
    ) : DisplayedMessage(messageId, sender, timestamp)

    companion object {
        fun fromMessage(message: Message): DisplayedMessage {
            val contentJson = JSONObject(message.content)
            return when (message.type) {
                "text" -> TextMessage(
                    messageId = message.messageId,
                    sender = contentJson.getString("sender"),
                    timestamp = message.timestamp,
                    text = contentJson.getString("text")
                )
                "file" -> FileMessage(
                    messageId = message.messageId,
                    sender = contentJson.getString("sender"),
                    timestamp = message.timestamp,
                    filename = contentJson.getString("filename"),
                    filetype = contentJson.getString("filetype"),
                    filesize = contentJson.getLong("filesize"),
                    cid = contentJson.getString("cid")
                )
                else -> throw IllegalArgumentException("Unknown message type: ${message.type}")
            }
        }
    }
}
