package edu.app.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.ArrayList

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var db = this.writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TODOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DELETE_TABLE_TODOS)
        onCreate(db)
    }

    override fun close() {
        super.close()
        db.close()
    }

    fun checkDBEmpty(): Int {
        val cursor = db.rawQuery(SELECT_COUNT_TODOS, null)
        cursor.moveToFirst();
        val count = cursor.getInt(0);
        cursor.close();

        return count
    }

    // loop through all rows and add to TODO list
    @SuppressLint("Range")
    fun allTodos(word: String): ArrayList<Todo> {
        val todosArrayList = ArrayList<Todo>()

        val projection = arrayOf(KEY_ID, USER_ID, TITLE, COMPLETED)
        var selection = "";
        if(word != "") {
            selection = "${TITLE} LIKE '%${word}%'"
        }
        Log.d("SQLITE", "" + selection)
        val selectionArgs = null //arrayOf(word)
        val sortOrder = "${TITLE} ASC"

        val cursor = db.query(
            TABLE_TODOS,
            projection,
            selection,
            null,
            null,
            null,
            sortOrder
        )

        with(cursor) {
            while (moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val userId = cursor.getInt(cursor.getColumnIndex(USER_ID))
                val title = cursor.getString(cursor.getColumnIndex(TITLE))
                val completed = cursor.getInt(cursor.getColumnIndex(COMPLETED))

                todosArrayList.add(Todo(id, userId, title, completed))
            }
        }
        cursor.close()

        return todosArrayList
    }

    fun addTodo(todo: Todo): Long {
        val values = ContentValues()
        values.put(KEY_ID, todo.id)
        values.put(USER_ID, todo.userId)
        values.put(TITLE, todo.title)
        values.put(COMPLETED, todo.completed)

        Log.d("SQLITE", values.toString())
        return db.insert(TABLE_TODOS, null, values)
    }

    companion object {
        var DATABASE_NAME = "todos_database"
        private val DATABASE_VERSION = 9
        private val TABLE_TODOS = "todos"
        private val KEY_ID = "id"
        private val USER_ID = "userId"
        private val TITLE = "title"
        private val COMPLETED = "completed"

        private val CREATE_TABLE_TODOS = ("CREATE TABLE "
                + TABLE_TODOS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + USER_ID + " INTEGER,"
                + TITLE + " TEXT,"
                + COMPLETED + " INTEGER);")

        private val DELETE_TABLE_TODOS = "DROP TABLE IF EXISTS $TABLE_TODOS"
        private val SELECT_COUNT_TODOS = "SELECT COUNT(*) FROM $TABLE_TODOS"
    }
}