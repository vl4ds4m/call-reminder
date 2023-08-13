package com.example.callreminder.ui.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callreminder.R

class ContactsAdapter(
    private val context: Context,
    private var list: List<Contact>,
    private val listener: ContactsClickListener
) : RecyclerView.Adapter<ContactsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContactsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.contact_card, parent, false)
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = list[position]
        holder.contactName.text = contact.name
        holder.contactPhone.text = contact.phone

        holder.contactCard.setOnClickListener { listener.onClick(list[holder.adapterPosition]) }

        holder.contactCard.setOnLongClickListener {
            listener.onLongClick(list[holder.adapterPosition], holder.contactCard)
            true
        }
    }

    fun updateContacts(newList: List<Contact>) {
        list = newList
        notifyDataSetChanged()
    }
}