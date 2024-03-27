package com.arnacon.chat_library

import android.net.Uri
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class DisplayedMessage(
    open val messageId: String,
    open val sender: String,
    open val formattedDate: String,
    open val formattedTime: String
) {
    data class TextMessage(
        override val messageId: String,
        override val sender: String,
        override val formattedDate: String,
        override val formattedTime: String,
        val text: String
    ) : DisplayedMessage(messageId, sender, formattedDate, formattedTime)

    data class FileMessage(
        override val messageId: String,
        override val sender: String,
        override val formattedDate: String,
        override val formattedTime: String,
        val filename: String,
        val filetype: String,
        val filesize: Long,
        val fileUri: Uri
    ) : DisplayedMessage(messageId, sender, formattedDate, formattedTime)

    companion object {
        fun fromMessage(message: Message, storage: Storage): DisplayedMessage {
            val contentJson = JSONObject(message.content)
            val date = Date(message.timestamp)
            val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(date)
            val formattedTime = timeFormat.format(date)

            return when (message.type) {
                "text" -> TextMessage(
                    messageId = message.messageId,
                    sender = contentJson.getString("sender"),
                    formattedDate = formattedDate,
                    formattedTime = formattedTime,
                    text = contentJson.getString("text")
                )
                "file" -> {
                    val fileUri = storage.getFileUri(message.messageId)
                    FileMessage(
                        messageId = message.messageId,
                        sender = contentJson.getString("sender"),
                        formattedDate = formattedDate,
                        formattedTime = formattedTime,
                        filename = contentJson.getString("filename"),
                        filetype = contentJson.getString("filetype"),
                        filesize = contentJson.getLong("filesize"),
                        fileUri = fileUri ?: Uri.EMPTY
                    )
                }
                else -> throw IllegalArgumentException("Unknown message type: ${message.type}")
            }
        }
    }
}
