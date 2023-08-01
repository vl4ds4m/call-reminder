package com.example.callreminder.ui

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
import com.example.callreminder.DATE_TIME_FORMATTER
import com.example.callreminder.R
import com.example.callreminder.db.AppDB
import com.example.callreminder.Note
import com.example.callreminder.db.getDB
import com.example.callreminder.services.CallNotification
import com.example.callreminder.services.channelID
import com.example.callreminder.ui.notes.NotesAdapter
import com.example.callreminder.ui.notes.NotesClickListener
import java.util.Calendar
import java.util.Date

private const val NEW_NOTE_REQUEST_CODE = 0
private const val UPDATED_NOTE_REQUEST_CODE = 1

const val noteExtra = "CallNote"
const val isNewNoteExtra = "isNewNote"

class MainActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var emptyListView: TextView

    private lateinit var notesDB: AppDB
    private lateinit var notes: MutableList<Note>

    private lateinit var selectedNote: Note

    private val notesClickListener = object : NotesClickListener {
        override fun onClick(note: Note) {
            selectedNote = note
            val intent = Intent(this@MainActivity, NoteActivity::class.java)
            intent.putExtra(isNewNoteExtra, false)
            intent.putExtra(noteExtra, note)
            startActivityForResult(intent, UPDATED_NOTE_REQUEST_CODE)
        }

        override fun onLongClick(note: Note, cardView: CardView) {
            selectedNote = note
            showPopUp(cardView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesDB = getDB(this)
        notes = ArrayList()
        notesAdapter = NotesAdapter(this, notes, notesClickListener)

        recyclerView = findViewById(R.id.notes)
        recyclerView.adapter = notesAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)

        emptyListView = findViewById(R.id.empty_list)
        emptyListView.visibility = if (notes.isEmpty()) View.VISIBLE else View.INVISIBLE

        createNotificationChannel()
    }

    override fun onResume() {
        super.onResume()
        updateListView()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.add_button -> {
                val intent = Intent(this, NoteActivity::class.java)
                intent.putExtra(isNewNoteExtra, true)
                startActivityForResult(intent, NEW_NOTE_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val newNote = data?.getSerializableExtra(noteExtra) as Note
            val newNoteRowId = notesDB.getDAO().insert(newNote)
            newNote.id = notesDB.getDAO().getNoteIdByRowId(newNoteRowId)
            if (requestCode == UPDATED_NOTE_REQUEST_CODE) {
                notesDB.getDAO().delete(selectedNote)
                cancelNotification(selectedNote)
            }
            scheduleNotification(newNote)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete -> {
                notesDB.getDAO().delete(selectedNote)
                cancelNotification(selectedNote)
                updateListView()
                return true
            }
        }
        return false
    }

    private fun updateListView() {
        notes.clear()
        notes.addAll(notesDB.getDAO().getAll())
        emptyListView.visibility = if (notes.isEmpty()) View.VISIBLE else View.INVISIBLE
        notesAdapter.notifyDataSetChanged()
    }

    private fun showPopUp(cardView: CardView) {
        val popupMenu = PopupMenu(this, cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.show()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelID,
            "Call Reminder Main Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(note: Note) {
        val intent = Intent(this, CallNotification::class.java)
        intent.putExtra(noteExtra, note)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            note.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val time = getTime(note.dateTime)
        val time = System.currentTimeMillis() + 3000
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    private fun getTime(dateTime: String): Long {
        val calendar = Calendar.getInstance()
        calendar.time = DATE_TIME_FORMATTER.parse(dateTime) as Date
        return calendar.timeInMillis
    }

    private fun cancelNotification(note: Note) {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            note.id,
            Intent(this, CallNotification::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(note.id)
    }
}
