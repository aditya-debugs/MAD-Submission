package com.example.exp10

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "StudentDB"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "students"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_ROLL = "roll_no"
        const val COL_BRANCH = "branch"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID     INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME   TEXT    NOT NULL,
                $COL_ROLL   TEXT    NOT NULL UNIQUE,
                $COL_BRANCH TEXT    NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertStudent(name: String, roll: String, branch: String): Long {
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_ROLL, roll)
            put(COL_BRANCH, branch)
        }
        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun getAllStudents(): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_NAME ORDER BY $COL_ID DESC",
            null
        )
    }

    fun updateStudent(id: Int, name: String, roll: String, branch: String): Int {
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_ROLL, roll)
            put(COL_BRANCH, branch)
        }
        return writableDatabase.update(
            TABLE_NAME,
            values,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteStudent(id: Int): Int {
        return writableDatabase.delete(
            TABLE_NAME,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun searchStudent(query: String): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COL_NAME LIKE ? ORDER BY $COL_ID DESC",
            arrayOf("%$query%")
        )
    }

    fun getStudentById(id: Int): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COL_ID = ? LIMIT 1",
            arrayOf(id.toString())
        )
    }
}

