package com.example.callreminder.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.callreminder.elements.Note

@Dao
interface AppDAO {
    @Insert(onConflict = REPLACE)
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT id FROM notes WHERE rowId = :rowId")
    fun getNoteIdByRowId(rowId: Long): Int

    @Query("SELECT * FROM notes ORDER BY time ASC")
    fun getAll(): List<Note>
}