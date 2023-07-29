package com.example.callreminder.ui.notes

import androidx.cardview.widget.CardView
import com.example.callreminder.Note

interface NotesClickListener {
    fun onClick(note: Note)
    fun onLongClick(note: Note, cardView: CardView)
}