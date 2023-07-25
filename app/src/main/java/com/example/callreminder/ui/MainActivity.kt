package com.example.callreminder.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import com.example.callreminder.R
import com.example.callreminder.db.AppDB
import com.example.callreminder.elements.Note
import com.example.callreminder.ui.notes.NotesAdapter
import com.example.callreminder.ui.notes.NotesClickListener

private const val newNoteReqCode = 1
private const val currentNoteReqCode = 2

class MainActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var emptyListView: TextView

    private val notesDB: AppDB by lazy {
        Room.databaseBuilder(this, AppDB::class.java, "CallReminderApp")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    private var notes: MutableList<Note> = ArrayList()

    private lateinit var selectedNote: Note

    private val notesClickListener = object : NotesClickListener {
        override fun onClick(note: Note) {
            val intent = Intent(applicationContext, NoteActivity::class.java)
            intent.putExtra("isNewNote", false)
            intent.putExtra("note", note)
            startActivityForResult(intent, currentNoteReqCode)
        }

        override fun onLongClick(note: Note, cardView: CardView) {
            selectedNote = note
            showPopUp(cardView)
        }
    }

    private fun showPopUp(cardView: CardView) {
        val popupMenu = PopupMenu(this, cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notes = ArrayList(notesDB.getDAO().getAll())
        notesAdapter = NotesAdapter(this, notes, notesClickListener)

        recyclerView = findViewById(R.id.notes)
        recyclerView.adapter = notesAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)

        emptyListView = findViewById(R.id.empty_list)
        emptyListView.visibility = if (notes.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.add_button -> {
                val intent = Intent(applicationContext, NoteActivity::class.java)
                intent.putExtra("isNewNote", true)
                startActivityForResult(intent, newNoteReqCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newNoteReqCode) {
            if (resultCode == Activity.RESULT_OK) {
                val newNote: Note = data?.getSerializableExtra("note") as Note
                notesDB.getDAO().insert(newNote)
                notes.clear()
                notes.addAll(notesDB.getDAO().getAll())
                emptyListView.visibility = if (notes.isEmpty()) View.VISIBLE else View.INVISIBLE
                notesAdapter.notifyDataSetChanged()
            }
        }

        if (requestCode == currentNoteReqCode) {
            if (resultCode == Activity.RESULT_OK) {
                val newNote: Note = data?.getSerializableExtra("note") as Note
                notesDB.getDAO().update(
                    newNote.id,
                    newNote.title,
                    newNote.description,
                    newNote.phone,
                    newNote.time
                )
                notes.clear()
                notes.addAll(notesDB.getDAO().getAll())
                notesAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete -> {
                notesDB.getDAO().delete(selectedNote)
                notes.remove(selectedNote)
                emptyListView.visibility = if (notes.isEmpty()) View.VISIBLE else View.INVISIBLE
                notesAdapter.notifyDataSetChanged()
                return true
            }
        }
        return false
    }
}
