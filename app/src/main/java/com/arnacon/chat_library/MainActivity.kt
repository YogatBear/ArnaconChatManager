package com.arnacon.chat_library

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatRoomActivity : AppCompatActivity(), ChatManager.ChatUpdateListener {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesAdapter: MessagesAdapter // Declare without initializing
    private lateinit var chatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview)

        val username = intent.getStringExtra("username") ?: "user123" // Get the username from the intent

        messagesAdapter = MessagesAdapter(mutableListOf(), username) // Initialize with the actual username
        setUpRecyclerView()

        messageEditText = findViewById(R.id.edit_gchat_message)
        sendButton = findViewById(R.id.button_gchat_send)

        chatManager = ChatManager(this, username) // Initialize ChatManager with the actual username
        chatManager.updateListener = this

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                chatManager.sendTextMessage(messageText)
                messageEditText.text.clear()
            }
        }
    }

    private fun setUpRecyclerView() {
        messagesRecyclerView = findViewById(R.id.recycler_gchat)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messagesAdapter
    }

    override fun onNewMessage(displayedMessage: DisplayedMessage) {
        runOnUiThread {
            Log.d("ChatRoomActivity", "Received new message: $displayedMessage")
            messagesAdapter.addMessage(displayedMessage)
            Log.d("ChatRoomActivity", "Message added to adapter")
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
