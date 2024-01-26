package com.arnacon.chat_library

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatRoomActivity : AppCompatActivity(), ChatManager.ChatUpdateListener {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var chatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview)  // Make sure this is the correct layout

        val username = intent.getStringExtra("username") ?: "user123"  // Default username

        messageEditText = findViewById(R.id.edit_gchat_message)  // ID from your layout
        sendButton = findViewById(R.id.button_gchat_send)  // ID from your layout
        messagesRecyclerView = findViewById(R.id.recycler_gchat)  // ID from your layout

        messagesAdapter = MessagesAdapter(mutableListOf(), username)
        messagesRecyclerView.adapter = messagesAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        chatManager = ChatManager(this, username) // Initialize ChatManager with the actual username
        chatManager.updateListener = this

        chatManager.loadRecentMessages()  // Load recent messages

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                chatManager.sendTextMessage(messageText)
                messageEditText.text.clear()
            }
        }
    }

    override fun onNewMessage(displayedMessage: DisplayedMessage) {
        runOnUiThread {
            messagesAdapter.addMessage(displayedMessage)
            messagesRecyclerView.scrollToPosition(messagesAdapter.itemCount - 1)
        }
    }

    override fun onNewMessages(displayedMessages: List<DisplayedMessage>) {
        runOnUiThread {
            displayedMessages.forEach { displayedMessage ->
                messagesAdapter.addMessage(displayedMessage)
            }
            messagesRecyclerView.scrollToPosition(messagesAdapter.itemCount - 1)
        }
    }
}
