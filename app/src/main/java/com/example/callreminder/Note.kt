package com.example.callreminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

val DATE_TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm Z", Locale.ENGLISH)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "phone") var phone: String = "",
    @ColumnInfo(name = "dateTime") var dateTime: String = ""
) : Serializable {
    override fun equals(other: Any?): Boolean {
        return (other is Note) && this.id == other.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }
}