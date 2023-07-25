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

    @Query(
        "UPDATE notes SET " +
                "title = :title, " +
                "description = :description, " +
                "phone = :phone, " +
                "time = :time " +
                "WHERE id = :id"
    )
    fun update(
        id: Int,
        title: String,
        description: String,
        phone: String,
        time: String
    )

    @Delete
    fun delete(note: Note)
}