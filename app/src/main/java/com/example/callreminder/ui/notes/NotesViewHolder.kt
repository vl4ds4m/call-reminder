package com.example.callreminder.ui.notes

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.callreminder.R

class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val noteCard: CardView
    val noteTitle: TextView
    val noteDateTime: TextView

    init {
        noteCard = itemView.findViewById(R.id.noteCard)
        noteTitle = itemView.findViewById(R.id.noteCardTitle)
        noteDateTime = itemView.findViewById(R.id.noteCardDateTime)
    }
}