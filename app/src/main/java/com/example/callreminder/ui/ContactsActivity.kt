package com.example.callreminder.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.callreminder.R
import com.example.callreminder.ui.contacts.Contact
import com.example.callreminder.ui.contacts.ContactsAdapter
import com.example.callreminder.ui.contacts.ContactsClickListener

private const val CONTACT_ID = ContactsContract.Contacts._ID
private const val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME
private const val HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER
private const val PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER
private const val PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID

private fun getPhones(contentResolver: ContentResolver): HashMap<Int, ArrayList<String>> {
    val phoneCursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(PHONE_NUMBER, PHONE_CONTACT_ID),
        null,
        null,
        null
    )
    val phones = HashMap<Int, ArrayList<String>>()

    if (phoneCursor != null) {
        while (phoneCursor.moveToNext()) {
            val idIndex = phoneCursor.getColumnIndex(PHONE_CONTACT_ID)
            val contactId = phoneCursor.getInt(idIndex)
            if (!phones.containsKey(contactId)) {
                phones[contactId] = ArrayList()
            }
            val phoneIndex = phoneCursor.getColumnIndex(PHONE_NUMBER)
            phones[contactId]!!.add(phoneCursor.getString(phoneIndex))
        }
        phoneCursor.close()
    }

    return phones
}

private fun getContacts(context: Context): ArrayList<Contact> {
    val contentResolver = context.contentResolver
    val phones = getPhones(contentResolver)
    val contactCursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        arrayOf(CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER),
        "$HAS_PHONE_NUMBER > 0",
        null,
        "$DISPLAY_NAME ASC"
    )
    val contacts = ArrayList<Contact>()

    if (contactCursor != null) {
        while (contactCursor.moveToNext()) {
            val idIndex = contactCursor.getColumnIndex(CONTACT_ID)
            val id = contactCursor.getInt(idIndex)
            if (phones.containsKey(id)) {
                val nameIndex = contactCursor.getColumnIndex(DISPLAY_NAME)
                val name = contactCursor.getString(nameIndex)
                phones[id]?.forEach { contacts.add(Contact(name, it)) }
            }
        }
        contactCursor.close()
    }

    return contacts
}

class ContactsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter

    private lateinit var contacts: MutableList<Contact>

    private val contactsClickListener = object : ContactsClickListener {
        override fun onClick(contact: Contact) {
            val intent = Intent()
            intent.putExtra(PHONE_EXTRA, contact.phone)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        override fun onLongClick(contact: Contact, cardView: CardView) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        contacts = getContacts(this)

        if (contacts.size == 0) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        contactsAdapter = ContactsAdapter(this, contacts, contactsClickListener)

        recyclerView = findViewById(R.id.contacts)
        recyclerView.adapter = contactsAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
    }
}