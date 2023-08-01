package com.example.callreminder.ui.contacts

import androidx.cardview.widget.CardView

interface ContactsClickListener {
    fun onClick(contact: Contact)

    fun onLongClick(contact: Contact, cardView: CardView)
}