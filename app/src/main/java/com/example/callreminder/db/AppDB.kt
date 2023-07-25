package com.example.callreminder.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.callreminder.elements.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun getDAO(): AppDAO
}