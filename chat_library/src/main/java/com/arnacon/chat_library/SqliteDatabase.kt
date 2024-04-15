package com.arnacon.chat_library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteIndex(private val dbHelper: DatabaseHelper) : Index {

    override fun storeMessage(message: Message, sessionId: String) {
        val db = dbHelper.writableDatabase
        val values = message.serialize().apply {
            // Add sessionId to the ContentValues
            put("sessionId", sessionId)
        }
        db.insert(DatabaseHelper.TABLE_MESSAGES, null, values)
        db.close()
    }


    override fun getMessages(start: Int, range: Int, sessionId: String): List<Message> {
        val db = dbHelper.readableDatabase
        val selection = "sessionId = ?"
        val selectionArgs = arrayOf(sessionId)

        val cursor = db.query(
            DatabaseHelper.TABLE_MESSAGES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "timestamp DESC",
            "$range OFFSET $start"
        )

        val messages = mutableListOf<Message>()
        while (cursor.moveToNext()) {
            messages.add(Message.fromCursor(cursor))
        }
        cursor.close()
        db.close()
        return messages.reversed()
    }

    override fun storeSession(session: ChatSession) {
        val db = dbHelper.writableDatabase
        val values = session.serialize()
        db.insert(DatabaseHelper.TABLE_SESSIONS, null, values)
        db.close()
    }

    override fun updateSession(session: ChatSession) {
        val db = dbHelper.writableDatabase
        val values = session.serialize()

        // Prepare criteria for updating the correct session
        val selection = "sessionId = ?"
        val selectionArgs = arrayOf(session.sessionId)

        // Perform the update on the database
        db.update(
            DatabaseHelper.TABLE_SESSIONS,
            values,
            selection,
            selectionArgs
        )

        db.close()
    }

    override fun sessionExists(sessionId: String): Boolean{
        val exists: Boolean

        val db = dbHelper.readableDatabase
        val selection = "sessionId = ?"
        val selectionArgs = arrayOf(sessionId)
        val cursor = db.query(
            DatabaseHelper.TABLE_SESSIONS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "lastMessage DESC"
        )

        exists = cursor.moveToFirst()

        cursor.close()
        return exists
    }

    override fun getSession(sessionId: String): ChatSession? {
        val db = dbHelper.readableDatabase
        val selection = "sessionId = ?"
        val selectionArgs = arrayOf(sessionId)
        val cursor = db.query(
            DatabaseHelper.TABLE_SESSIONS,
            null, // all columns
            selection, // selection condition
            selectionArgs, // selection arguments
            null, // groupBy
            null, // having
            "lastMessage DESC" // orderBy
        )

        val session: ChatSession? = if (cursor.moveToFirst()) {
            ChatSession.fromCursor(cursor)
        } else {
            null // Return null if the session wasn't found
        }

        cursor.close()
        db.close()
        return session
    }


    override fun getSessions(): List<ChatSession> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_SESSIONS,
            null,
            null,
            null,
            null,
            null,
            "lastMessage DESC"
        )

        val sessions = mutableListOf<ChatSession>()
        while (cursor.moveToNext()) {
            sessions.add(ChatSession.fromCursor(cursor))
        }
        cursor.close()
        db.close()
        return sessions
    }
}


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "chat_database.db"
        private const val DATABASE_VERSION = 1 // Updated version
        const val TABLE_MESSAGES = "messages"
        const val TABLE_SESSIONS = "sessions"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val messageStructure = Message.getStructure()
        val messageColumns = messageStructure.entries.joinToString(", ") { "${it.key} ${it.value}" }
        val createMessagesTableStatement = "CREATE TABLE $TABLE_MESSAGES ($messageColumns)"

        val sessionStructure = ChatSession.getStructure()
        val sessionColumns = sessionStructure.entries.joinToString(", ") { "${it.key} ${it.value}" }
        val createSessionsTableStatement = "CREATE TABLE $TABLE_SESSIONS ($sessionColumns)"

        db.execSQL(createMessagesTableStatement)
        db.execSQL(createSessionsTableStatement)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SESSIONS")
        onCreate(db) // Recreate the database
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}