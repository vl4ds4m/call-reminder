package com.example.callreminder.ui.contacts

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.callreminder.R

class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val contactCard: CardView
    val contactName: TextView
    val contactPhone: TextView

    init {
        contactCard = itemView.findViewById(R.id.contact_card)
        contactName = itemView.findViewById(R.id.contact_card_name)
        contactPhone = itemView.findViewById(R.id.contact_card_phone)
    }
}