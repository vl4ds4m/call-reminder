package com.example.callreminder.ui.notes

import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callreminder.DATE_TIME_FORMATTER
import com.example.callreminder.Note
import com.example.callreminder.R
import com.example.callreminder.ui.makeDateString
import com.example.callreminder.ui.makeTimeString

class NotesAdapter(
    private val context: Context,
    private val list: List<Note>,
    private val listener: NotesClickListener
) : RecyclerView.Adapter<NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NotesViewHolder(
            LayoutInflater.from(context).inflate(R.layout.note_card, parent, false)
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = list[position]
        holder.noteTitle.text = note.title
        holder.noteDateTime.text = getPrettyDateTime(note.dateTime)

        holder.noteCard.setOnClickListener { listener.onClick(list[holder.adapterPosition]) }

        holder.noteCard.setOnLongClickListener {
            listener.onLongClick(list[holder.adapterPosition], holder.noteCard)
            true
        }
    }

    private fun getPrettyDateTime(dateTime: String): String {
        val calendar = Calendar.getInstance()
        calendar.time = DATE_TIME_FORMATTER.parse(dateTime)

        val year = calendar[java.util.Calendar.YEAR]
        val month = calendar[java.util.Calendar.MONTH]
        val day = calendar[java.util.Calendar.DAY_OF_MONTH]
        val hour = calendar[java.util.Calendar.HOUR_OF_DAY]
        val minute = calendar[java.util.Calendar.MINUTE]

        return makeDateString(year, month, day) + "  " + makeTimeString(hour, minute)
    }
}