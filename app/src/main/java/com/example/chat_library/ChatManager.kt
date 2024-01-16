package com.example.chat_library

import android.content.Context

class ChatManager(private val context: Context) {
    private val chatIndex = Index(context)

    fun sendMessage(sender: String, type: String, content: String) {
        // Create a new Message object using its constructor for new messages
        val newMessage = Message(sender, type, content)

        // Store the message in the database
        chatIndex.storeMessage(newMessage)
    }

    fun getRecentMessages(count: Int = 10): List<Message> {
        // Retrieve the most recent 'count' messages
        return chatIndex.getMessages(0, count)
    }

    // Additional functionalities can be added here
}
