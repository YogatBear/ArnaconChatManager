package com.arnacon.chat_library

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.tasks.await
import android.util.Log
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class FirestoreMessageReceiveTest {

    @Test
    fun testReceiveMessage() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val firestore = FirebaseFirestore.getInstance()

        // Simulate a message sent to "user1"
        val contentJson = JSONObject().apply {
            put("sender", "user2")
            put("text", "Hello from user2!")
        }.toString()
        val simulatedMessage = hashMapOf(
            "messageId" to "msg123",
            "timestamp" to System.currentTimeMillis(),
            "type" to "text",
            "content" to contentJson
        )
        firestore.collection("messages").add(simulatedMessage).await()

        val chatManager = ChatManager(appContext, "user1")
        var messageReceived = false

        // Wait for a new message to be received or timeout after 10 seconds
        withTimeoutOrNull(10000L) {
            while (!messageReceived) {
                val recentMessages = chatManager.getRecentMessages()
                for (message in recentMessages) {
                    val messageContentJson = JSONObject(message.content)
                    val sender = messageContentJson.optString("sender")
                    val text = messageContentJson.optString("text")
                    if (sender == "user2" && text == "Hello from user2!") {
                        messageReceived = true
                        Log.d("ReceiveTest", "Message received from firestore")
                        break
                    }
                }
                delay(500)  // Check every 500ms
            }
        }

        assertTrue("No new message received from user2", messageReceived)
    }
}
