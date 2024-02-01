package com.arnacon.chat_library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteIndex(private val dbHelper: DatabaseHelper) : Index {

    override fun storeMessage(message: Message) {
        val db = dbHelper.writableDatabase
        val values = message.serialize()
        db.insert(DatabaseHelper.TABLE_MESSAGES, null, values)
        db.close()
    }

    override fun getMessages(start: Int, range: Int): List<Message> {
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
            messages.add(Message.fromCursor(cursor))
        }
        cursor.close()
        db.close()
        return messages.reversed()
    }
}


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "chat_database.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_MESSAGES = "messages"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val structure = Message.getStructure()
        val columns = structure.entries.joinToString(", ") { "${it.key} ${it.value}" }
        val createTableStatement = "CREATE TABLE $TABLE_MESSAGES ($columns)"
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database version upgrades here
    }
}
