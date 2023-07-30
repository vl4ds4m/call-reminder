package com.example.callreminder.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.callreminder.Note

private var database: AppDB? = null

fun getDB(context: Context): AppDB {
    if (database == null) {
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDB::class.java,
            "CallReminderApp"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    return database as AppDB
}

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun getDAO(): AppDAO
}