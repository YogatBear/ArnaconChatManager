package com.arnacon.chat_app

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arnacon.chat_library.ChatManager
import com.arnacon.chat_library.DisplayedMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRoomActivity : AppCompatActivity(), ChatManager.ChatUpdateListener {

    private val imageRequestCode = 1

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview)  // Assuming this is your layout file

        val username = intent.getStringExtra("username") ?: "user123"

        messageEditText = findViewById(R.id.edit_gchat_message)
        sendButton = findViewById(R.id.button_gchat_send)
        messagesRecyclerView = findViewById(R.id.recycler_gchat)

        messageAdapter = MessageAdapter(mutableListOf(), username) { fileUri ->
            openFile(fileUri)
        }
        messagesRecyclerView.adapter = messageAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        chatManager = ChatManager(this, username)
        chatManager.updateListener = this

        chatManager.loadRecentMessages(0,10,sessionId)

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                // Use the new newMessage function with callbacks
                newMessage("text", messageText, null)
                messageEditText.text.clear()
            }
        }

        val sendImageButton = findViewById<Button>(R.id.button_send_image)
        sendImageButton.setOnClickListener {
            openImagePicker()
        }
    }

    override fun onNewMessage(displayedMessage: DisplayedMessage) {
        runOnUiThread {
            messageAdapter.addMessage(displayedMessage)
            messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    override fun onNewMessages(displayedMessages: List<DisplayedMessage>) {
        runOnUiThread {
            displayedMessages.forEach { displayedMessage ->
                messageAdapter.addMessage(displayedMessage)
            }
            messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)
    }

    private fun openFile(fileUri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(fileUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                withContext(Dispatchers.Main) {
                    val imageView: ImageView = findViewById(R.id.imageView)
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error opening file: ${e.message}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Use the new newMessage function with callbacks for image
                newMessage("file", "Image", uri)
            }
        }
    }

    private fun newMessage(messageType: String, content: String, uri: Uri?){
        chatManager.newMessage(messageType, content, uri,
            onSuccess = { newMessage ->
                chatManager.storeMessage(newMessage)
                chatManager.uploadMessage(newMessage)
            },
            onError = { error ->
                Log.e("ChatRoomActivity", "Failed to create message: ${error.message}")
            }
        )
    }
}