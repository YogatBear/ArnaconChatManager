package com.arnacon.chat_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class UsernameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activityusername)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmitUsername)

        buttonSubmit.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            if (username.isNotEmpty()) {
                val intent = Intent(this, ChatRoomActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish() // Close this activity
            }
        }
    }
}
