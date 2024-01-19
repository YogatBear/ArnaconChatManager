package com.arnacon.chat_library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


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
