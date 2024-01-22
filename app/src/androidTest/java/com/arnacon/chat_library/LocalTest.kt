package com.arnacon.chat_library
import android.util.Log

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun testChatManager() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.chat_library", appContext.packageName)

        val chatManager = ChatManager(appContext, "user123")
        val testType = "text"
        val testContent = "Test message"

        // Call sendMessage
        chatManager.sendTextMessage(testContent)
        chatManager.sendTextMessage(testContent)

        // Retrieve recent messages
        val recentMessages = chatManager.getRecentMessages()

        // Assertions
        assertTrue("Recent messages should include the sent message", recentMessages.any {
            it.sender == "user123" && it.type == testType && it.content == testContent
        })

        // Logging for detailed observation
        recentMessages.forEach { message ->
            Log.d("TestChatManager", "Retrieved message: $message, ${message.messageId}, ${message.timestamp}")
        }
    }
}