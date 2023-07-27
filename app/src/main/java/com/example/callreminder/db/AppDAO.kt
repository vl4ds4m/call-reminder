package com.example.callreminder.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.callreminder.elements.Note

@Dao
interface AppDAO {
    @Insert(onConflict = REPLACE)
    fun insert(note: Note)

    @Query("SELECT * FROM notes ORDER BY time ASC")
    fun getAll(): List<Note>

    @Delete
    fun delete(note: Note)
}