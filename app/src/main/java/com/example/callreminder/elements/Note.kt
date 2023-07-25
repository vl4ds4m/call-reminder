package com.example.callreminder.elements

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "phone") var phone: String = "",
    @ColumnInfo(name = "time") var time: String = ""
) : Serializable {
    override fun equals(other: Any?): Boolean {
        return (other is Note) && this.id == other.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }
}