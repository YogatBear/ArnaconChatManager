package com.arnacon.chat_library

import android.content.ContentValues
import android.content.Context

class Index(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context.applicationContext)

    fun storeMessage(message: Message) {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        Message.getStructure().forEach { (key, _) ->
            val field = message::class.java.getDeclaredField(key)
            field.isAccessible = true
            values.put(key, field.get(message).toString())
        }
        db.insert(DatabaseHelper.TABLE_MESSAGES, null, values)
        db.close()
    }



    fun getMessages(start: Int = 0, range: Int = 10): List<Message> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_MESSAGES,
            null,
            null,
            null,
            null,
            null,
            "timestamp DESC",
            "$start, $range"
        )

        val messages = mutableListOf<Message>()
        while (cursor.moveToNext()) {
            messages.add(Message(cursor))
        }
        cursor.close()
        db.close()
        return messages
    }
}
