package eu.tutorials.loginsignupsql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class DatabaseHelper(private val context: Context):
             SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?){
         val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                 "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                 "$COLUMN_USERNAME TEXT, " +
                 "$COLUMN_PASSWORD TEXT)")

         db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db : SQLiteDatabase?, oldVersion: Int, newVersion: Int){
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertUser(username: String, password: String): Long {
        val values = ContentValues().apply{
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }

        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }

    fun readUser(username: String, password: String): Boolean{
        val db =  readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }


    fun exportDatabase() {
        val databasePath = this.context.getDatabasePath(DATABASE_NAME).absolutePath
        val externalStoragePath = this.context.getExternalFilesDir(null)?.absolutePath + "/UserDatabase.db"

        val source = File(databasePath)
        val destination = File(externalStoragePath)

        try {
            FileInputStream(source).use { input ->
                FileOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("Database Export", "Database exported to: $externalStoragePath")
        } catch (e: IOException) {
            Log.e("Database Export", "Failed to export database: ${e.message}")
        }
    }

    //for additional page

}