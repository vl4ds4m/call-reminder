package com.example.callreminder.ui

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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

const val NOTE_EXTRA = "CallNote"
const val IS_NEW_NOTE_EXTRA = "isNewNote"

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
            intent.putExtra(IS_NEW_NOTE_EXTRA, false)
            intent.putExtra(NOTE_EXTRA, note)
            updateNoteLauncher.launch(intent)
        }

        override fun onLongClick(note: Note, cardView: CardView) {
            selectedNote = note
            showPopUp(cardView)
        }
    }

    private val createNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val newNote = data?.getSerializableExtra(NOTE_EXTRA) as Note

            val newNoteRowId = notesDB.getDAO().insert(newNote)
            newNote.id = notesDB.getDAO().getNoteIdByRowId(newNoteRowId)

            scheduleNotification(newNote)
        }
    }

    private val updateNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val newNote = data?.getSerializableExtra(NOTE_EXTRA) as Note

            val newNoteRowId = notesDB.getDAO().insert(newNote)
            newNote.id = notesDB.getDAO().getNoteIdByRowId(newNoteRowId)

            notesDB.getDAO().delete(selectedNote)
            cancelNotification(selectedNote)

            scheduleNotification(newNote)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            AlertDialog.Builder(this)
                .setMessage(R.string.post_notifications_permission_rejection_text)
                .show()
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
        requestPostNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        updateListView()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.add_button -> {
                val intent = Intent(this, NoteActivity::class.java)
                intent.putExtra(IS_NEW_NOTE_EXTRA, true)
                createNoteLauncher.launch(intent)
            }
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

    private fun requestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun scheduleNotification(note: Note) {
        val intent = Intent(this, CallNotification::class.java)
        intent.putExtra(NOTE_EXTRA, note)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            note.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime(note.dateTime)
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
