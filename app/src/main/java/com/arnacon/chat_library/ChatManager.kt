package com.arnacon.chat_library

import android.content.Context

class ChatManager(private val context: Context, private val user: String) {
    private val chatIndex = Index(context)
    private val pubSub = PubSub(context, user)

    init {
        pubSub.listenForNewMessages { message ->
            // Handle the new message here
            onNewMessageReceived(message)
        }
    }

    fun sendMessage(type: String, content: String) {
        // Create a new Message object using its constructor for new messages
        val newMessage = Message(user, type, content)
        // Store the message in the database
        chatIndex.storeMessage(newMessage)
        // Upload the message to firestore
        pubSub.uploadMessage(newMessage)
    }

    private fun onNewMessageReceived(newMessage: Message) {
        chatIndex.storeMessage(newMessage)
    }

    fun getRecentMessages(count: Int = 10): List<Message> {
        // Retrieve the most recent 'count' messages
        return chatIndex.getMessages(0, count)
    }

    // Additional functionalities can be added here
}
