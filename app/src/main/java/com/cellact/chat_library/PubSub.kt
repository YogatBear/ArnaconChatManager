package com.cellact.chat_library

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentChange


class PubSub(private val context: Context, private val currentUser: String) {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun uploadMessage(message: Message) {
        val testCollection = firestore.collection("messages")
        testCollection.document(message.messageId).set(message)
    }

    fun listenForNewMessages(onNewMessageReceived: (Message) -> Unit) {
        firestore.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { doc ->
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val message = doc.document.toObject(Message::class.java)
                        if (message.sender != currentUser) {
                            onNewMessageReceived(message)
                        }
                    }
                }
            }
    }
}
