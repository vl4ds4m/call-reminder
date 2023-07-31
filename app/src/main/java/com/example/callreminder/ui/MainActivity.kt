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
import com.example.callreminder.R
import com.example.callreminder.db.AppDB
import com.example.callreminder.Note
import com.example.callreminder.db.getDB
import com.example.callreminder.services.CallNotification
import com.example.callreminder.services.channelID
import com.example.callreminder.ui.notes.NotesAdapter
import com.example.callreminder.ui.notes.NotesClickListener
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

private const val NEW_NOTE_REQUEST_CODE = 0
private const val UPDATED_NOTE_REQUEST_CODE = 1

const val noteExtra = "CallNote"
const val isNewNoteExtra = "isNewNote"

class MainActivity : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var emptyListView: TextView

    private lateinit var notesDB: AppDB

    private var notes: MutableList<Note> = ArrayList()

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
        notes = ArrayList(notesDB.getDAO().getAll())
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
        updateListView()
        super.onResume()
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
            val note = data?.getSerializableExtra(noteExtra) as Note
            if (requestCode == NEW_NOTE_REQUEST_CODE) {
                val newNoteRowId = notesDB.getDAO().insert(note)
                note.id = notesDB.getDAO().getNoteIdByRowId(newNoteRowId)
            } else if (requestCode == UPDATED_NOTE_REQUEST_CODE) {
                note.id = selectedNote.id
                notesDB.getDAO().update(note)
            }
            updateListView()
            scheduleNotification(note)
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
//        val time = getTime(note)
        val time = System.currentTimeMillis() + 3000
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    private fun getTime(note: Note): Long {
        val localDate = LocalDate.parse(note.time.split(" ")[0])
        val localTime = LocalTime.parse(note.time.split(" ")[1])

        val year = localDate.year
        val month = localDate.monthValue - 1
        val day = localDate.dayOfMonth
        val hour = localTime.hour
        val minute = localTime.minute

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)

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
