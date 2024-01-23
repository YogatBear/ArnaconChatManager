package com.arnacon.chat_library

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class FirestoreOperationTest {

    @Test
    fun testSendMessage() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val chatManager = ChatManager(appContext, "user123")
        val firestore = FirebaseFirestore.getInstance()

        val messageContent = "Test message"
        chatManager.sendTextMessage(messageContent)

        // Listen for changes in Firestore for up to 10 seconds
        val messageFound = withTimeoutOrNull(10000L) {
            var found = false
            val listenerRegistration = firestore.collection("messages")
                .addSnapshotListener { snapshot: QuerySnapshot?, _ ->
                    snapshot?.documents?.forEach { document ->
                        val contentJson = JSONObject(document.getString("content"))
                        val textInMessage = contentJson.optString("text")
                        if (textInMessage == messageContent) {
                            found = true
                        }
                    }
                }

            while (!found) {
                delay(100)  // Check every 100ms
            }
            listenerRegistration.remove()  // Stop listening to changes
            found
        }

        assertNotNull("Specific message was not found in Firestore within the time limit", messageFound)
    }
}