package com.arnacon.chat_library

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentChange
import org.json.JSONObject


class FirestoreMessaging(private val currentUser: String): PubSub {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun uploadMessage(message: Message, recipient: String) {
        val userCollection = firestore.collection(recipient)
        userCollection.document(message.messageId).set(message)
    }

    override fun listenForNewMessages(onNewMessageReceived: (Message) -> Unit) {
        firestore.collection(currentUser)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { doc ->
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val message = doc.document.toObject(Message::class.java)
                        onNewMessageReceived(message)
                    }
                }
            }
    }
}
