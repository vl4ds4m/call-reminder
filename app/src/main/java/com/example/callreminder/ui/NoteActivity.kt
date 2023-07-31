package com.example.callreminder.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.example.callreminder.R
import com.example.callreminder.Note
import com.example.callreminder.DATE_TIME_FORMATTER
import java.util.Calendar
import java.util.Date

fun getDateString(dateTime: String): String {
    val calendar = Calendar.getInstance()
    calendar.time = DATE_TIME_FORMATTER.parse(dateTime) as Date
    val year = calendar[Calendar.YEAR]
    val monthNum = calendar[Calendar.MONTH] + 1
    val day = calendar[Calendar.DAY_OF_MONTH]
    return makeDateString(year, monthNum, day)
}

fun makeDateString(year: Int, monthNum: Int, day: Int): String {
    val month = when (monthNum) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> "???"
    }
    return "$day $month $year"
}

fun getTimeString(dateTime: String): String {
    val calendar = Calendar.getInstance()
    calendar.time = DATE_TIME_FORMATTER.parse(dateTime) as Date
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    return makeTimeString(hour, minute)
}

fun makeTimeString(hour: Int, minute: Int): String {
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
    private lateinit var timePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        noteTitle = findViewById(R.id.noteTitle)
        noteDescription = findViewById(R.id.noteDescription)
        notePhone = findViewById(R.id.notePhone)
        noteDate = findViewById(R.id.noteDate)
        noteTime = findViewById(R.id.noteTime)

        createDatePicker()
        createTimePicker()

        val isNewNote = intent.getBooleanExtra(isNewNoteExtra, false)

        if (isNewNote) {
            noteTitle.setText("")
            noteDescription.setText("")
            notePhone.setText("")
            noteDate.text = ""
            noteTime.text = ""
        } else {
            val note = intent.getSerializableExtra(noteExtra) as Note
            noteTitle.setText(note.title)
            noteDescription.setText(note.description)
            notePhone.setText(note.phone)
            noteDate.text = getDateString(note.dateTime)
            noteTime.text = getTimeString(note.dateTime)
        }
    }

    private fun createDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            noteDate.text = makeDateString(year, month + 1, day)
        }

        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(this, style, listener, year, month, day)
    }

    private fun createTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            this.timePicker = timePicker
            noteTime.text = makeTimeString(hour, minute)
        }

        val calendar = Calendar.getInstance()
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
                "Please, fill in all required fields.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val newNote = Note()
        newNote.title = noteTitle.text.toString()
        newNote.description = noteDescription.text.toString()
        newNote.phone = notePhone.text.toString()
        setDateTime(newNote)

        val intent = Intent()
        intent.putExtra(noteExtra, newNote)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setDateTime(note: Note) {
        val year = datePickerDialog.datePicker.year
        val month = datePickerDialog.datePicker.month
        val day = datePickerDialog.datePicker.dayOfMonth
        val hour = timePicker.hour
        val minute = timePicker.minute

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        note.dateTime = DATE_TIME_FORMATTER.format(calendar.time)
    }
}