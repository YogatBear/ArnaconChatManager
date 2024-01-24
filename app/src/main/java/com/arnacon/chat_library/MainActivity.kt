package com.arnacon.chat_library

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.arnacon.chat_library.R


class ChatRoomActivity : AppCompatActivity(), ChatManager.ChatUpdateListener {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private val messagesAdapter = MessagesAdapter(mutableListOf(), "user123") // Replace "user123" with current user
    private lateinit var chatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview)

        messagesRecyclerView = findViewById(R.id.recycler_gchat)
        messageEditText = findViewById(R.id.edit_gchat_message)
        sendButton = findViewById(R.id.button_gchat_send)
        messagesRecyclerView.adapter = messagesAdapter

        chatManager = ChatManager(this, "user123") // Replace "user123" with the actual user identifier
        chatManager.updateListener = this

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                chatManager.sendTextMessage(messageText)
                messageEditText.text.clear()
            }
        }

        // Implement message reception logic...
    }

    override fun onNewMessage(displayedMessage: DisplayedMessage) {
        runOnUiThread {
            messagesAdapter.addMessage(displayedMessage)
        }
    }
}
