package com.example.callreminder.ui.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callreminder.Note
import com.example.callreminder.R
import com.example.callreminder.ui.getDateString
import com.example.callreminder.ui.getTimeString

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
        val prettyDateTime = getDateString(note.dateTime) + "  " + getTimeString(note.dateTime)

        holder.noteTitle.text = note.title
        holder.noteDateTime.text = prettyDateTime

        holder.noteCard.setOnClickListener { listener.onClick(list[holder.adapterPosition]) }

        holder.noteCard.setOnLongClickListener {
            listener.onLongClick(list[holder.adapterPosition], holder.noteCard)
            true
        }
    }
}