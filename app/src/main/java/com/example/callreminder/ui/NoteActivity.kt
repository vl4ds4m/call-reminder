package com.example.callreminder.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.callreminder.R
import com.example.callreminder.Note
import com.example.callreminder.DATE_TIME_FORMATTER
import java.util.Calendar
import java.util.Date

fun makeDateString(context: Context, year: Int, monthNum: Int, day: Int): String {
    val month = when (monthNum) {
        0 -> context.getString(R.string.jan)
        1 -> context.getString(R.string.feb)
        2 -> context.getString(R.string.mar)
        3 -> context.getString(R.string.apr)
        4 -> context.getString(R.string.may)
        5 -> context.getString(R.string.jun)
        6 -> context.getString(R.string.jul)
        7 -> context.getString(R.string.aug)
        8 -> context.getString(R.string.sep)
        9 -> context.getString(R.string.oct)
        10 -> context.getString(R.string.nov)
        11 -> context.getString(R.string.dec)
        else -> "???"
    }
    return "$day $month $year"
}

fun makeTimeString(hour: Int, minute: Int): String {
    if (minute < 10) {
        return "$hour:0$minute"
    }
    return "$hour:$minute"
}

const val PHONE_EXTRA = "phoneExtra"

class NoteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var noteTitle: EditText
    private lateinit var noteDescription: EditText
    private lateinit var notePhone: EditText
    private lateinit var noteDate: TextView
    private lateinit var noteTime: TextView

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var calendar: Calendar

    private val selectContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val phone = data?.getStringExtra(PHONE_EXTRA)
            if (phone != null) {
                notePhone.setText(phone)
            } else {
                Toast.makeText(
                    this,
                    R.string.no_contact_text,
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        setContentView(R.layout.activity_note)

        noteTitle = findViewById(R.id.note_title)
        noteDescription = findViewById(R.id.note_description)
        notePhone = findViewById(R.id.note_phone)
        noteDate = findViewById(R.id.note_date)
        noteTime = findViewById(R.id.note_time)

        calendar = Calendar.getInstance()
        val isNewNote = intent.getBooleanExtra(IS_NEW_NOTE_EXTRA, false)
        if (isNewNote) {
            noteTitle.setText("")
            noteDescription.setText("")
            notePhone.setText("")
        } else {
            val note = intent.getSerializableExtra(NOTE_EXTRA) as Note
            noteTitle.setText(note.title)
            noteDescription.setText(note.description)
            notePhone.setText(note.phone)
            calendar.time = DATE_TIME_FORMATTER.parse(note.dateTime) as Date
        }
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        noteDate.text = makeDateString(this, year, month, day)
        noteTime.text = makeTimeString(hour, minute)

        createDatePicker()
        createTimePicker()
    }

    private fun createDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            noteDate.text = makeDateString(this, year, month, day)
        }

        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        datePickerDialog = DatePickerDialog(this, listener, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
    }

    private fun createTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            noteTime.text = makeTimeString(hour, minute)
        }

        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        timePickerDialog = TimePickerDialog(this, listener, hour, minute, true)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.note_date -> datePickerDialog.show()
            R.id.note_time -> timePickerDialog.show()
            R.id.save_button -> addNote()
            R.id.contact_button -> selectContact()
        }
    }

    private fun selectContact() {
        val permission = Manifest.permission.READ_CONTACTS
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, ContactsActivity::class.java)
            selectContactLauncher.launch(intent)
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun addNote() {
        if (noteTitle.text.isBlank() || noteDate.text.isBlank() || noteTime.text.isBlank()) {
            Toast.makeText(
                this,
                R.string.empty_field_warning_text,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val newNote = Note()
        newNote.title = noteTitle.text.toString()
        newNote.description = noteDescription.text.toString()
        newNote.phone = notePhone.text.toString()
        newNote.dateTime = DATE_TIME_FORMATTER.format(calendar.time)

        val intent = Intent()
        intent.putExtra(NOTE_EXTRA, newNote)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}