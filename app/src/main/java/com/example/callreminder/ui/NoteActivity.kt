package com.example.callreminder.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

class NoteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var noteTitle: EditText
    private lateinit var noteDescription: EditText
    private lateinit var notePhone: EditText
    private lateinit var noteDate: TextView
    private lateinit var noteTime: TextView

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteDescription = findViewById(R.id.noteDescription)
        notePhone = findViewById(R.id.notePhone)
        noteDate = findViewById(R.id.noteDate)
        noteTime = findViewById(R.id.noteTime)

        calendar = Calendar.getInstance()
        val isNewNote = intent.getBooleanExtra(isNewNoteExtra, false)
        if (isNewNote) {
            noteTitle.setText("")
            noteDescription.setText("")
            notePhone.setText("")
        } else {
            val note = intent.getSerializableExtra(noteExtra) as Note
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

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(this, style, listener, year, month, day)
    }

    private fun createTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            noteTime.text = makeTimeString(hour, minute)
        }

        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        val style = AlertDialog.THEME_HOLO_LIGHT

        timePickerDialog = TimePickerDialog(this, style, listener, hour, minute, true)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.noteDate -> {
                datePickerDialog.show()
            }

            R.id.noteTime -> {
                timePickerDialog.show()
            }

            R.id.save_button -> {
                addNote()
            }
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
        intent.putExtra(noteExtra, newNote)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}