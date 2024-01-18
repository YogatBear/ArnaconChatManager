package com.cellact.chat_library

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore


class ChatManager(private val context: Context, private val user: String) {
    private val chatIndex = Index(context)
    private val pubSub = PubSub(context, user)

    private lateinit var firestore: FirebaseFirestore


    fun sendMessage(type: String, content: String) {
        // Create a new Message object using its constructor for new messages
        val newMessage = Message(user, type, content)
        // Store the message in the database
        chatIndex.storeMessage(newMessage)
        // Upload the message to firestore
        pubSub.uploadMessage(newMessage)
    }

    fun startListeningForMessages() {
        pubSub.listenForNewMessages { message ->
            // This is where you handle the new message.
            // For example, you can update the UI, store the message, etc.
            // onNewMessageReceived(message)
        }
    }

    fun getRecentMessages(count: Int = 10): List<Message> {
        // Retrieve the most recent 'count' messages
        return chatIndex.getMessages(0, count)
    }

    // Additional functionalities can be added here
}
