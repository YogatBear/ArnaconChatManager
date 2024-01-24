package com.arnacon.chat_library
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.File
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FirestoreFileOperationTest {

    @Test
    fun testSendFileMessage() {
        runBlocking {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            val chatManager = ChatManager(appContext, "user123")
            val firestore = FirebaseFirestore.getInstance()

            // Creating a temporary test file
            val testFile = createTestFile(appContext.filesDir, "testFile.txt", "Test file content")

            // Sending the file message
            chatManager.sendFileMessage("file", testFile)

            // Listen for changes in Firestore for up to 10 seconds
            val messageFound = withTimeoutOrNull(10000L) {
                var found = false
                val listenerRegistration = firestore.collection("messages")
                    .whereEqualTo("type", "file")
                    .addSnapshotListener { snapshot: QuerySnapshot?, _ ->
                        if (snapshot != null) {
                            for (document in snapshot.documents) {
                                // Log each document's data
                                Log.d("FirestoreTest", "Document: ${document.id}, Data: ${document.data}")
                                if (document.getString("content")?.contains(testFile.name) == true) {
                                    found = true
                                }
                            }
                        }
                    }

                while (!found) {
                    delay(500)  // Check every 500ms
                }
                listenerRegistration.remove()  // Stop listening to changes
                found
            }

            assertNotNull("Specific file message was not found in Firestore within the time limit", messageFound)

            // Cleanup: Delete the test file
            testFile.delete()
        }
    }

    private fun createTestFile(dir: File, fileName: String, content: String): File {
        val file = File(dir, fileName)
        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(content.toByteArray())
            }
        } catch (e: IOException) {
            fail("Failed to create test file")
        }
        return file
    }
}
