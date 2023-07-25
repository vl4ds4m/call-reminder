package com.example.callreminder.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.callreminder.R
import com.example.callreminder.elements.Note
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

class NoteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescr: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText

    private var isNewNote: Boolean = false
    private lateinit var currentNote: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescr = findViewById(R.id.editTextDescr)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)

        isNewNote = intent.getBooleanExtra("isNewNote", false)

        if (isNewNote) {
            currentNote = Note()
            editTextTitle.setText("")
            editTextDescr.setText("")
            editTextPhone.setText("")
            editTextDate.setText("")
            editTextTime.setText("")
        } else {
            currentNote = intent.getSerializableExtra("note") as Note
            editTextTitle.setText(currentNote.title)
            editTextDescr.setText(currentNote.description)
            editTextPhone.setText(currentNote.phone)
            editTextDate.setText(currentNote.time.split(" ")[0])
            editTextTime.setText(currentNote.time.split(" ")[1])
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.save_button -> {
                fillNote(currentNote)
            }
        }
    }

    private fun fillNote(note: Note) {
        if (
            editTextTitle.text.isBlank() ||
            editTextPhone.text.isBlank() ||
            editTextDate.text.isBlank() ||
            editTextTime.text.isBlank()
        ) {
            Toast.makeText(
                this,
                "Please, fill in all required fields.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            LocalDate.parse(editTextDate.text.toString())
        } catch (e: DateTimeParseException) {
            Toast.makeText(
                this,
                "Please, type correct date.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            LocalTime.parse(editTextTime.text.toString())
        } catch (e: DateTimeParseException) {
            Toast.makeText(
                this,
                "Please, type correct time.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        note.title = editTextTitle.text.toString()
        note.description = editTextDescr.text.toString()
        note.phone = editTextPhone.text.toString()
        note.time = editTextDate.text.toString() + " " + editTextTime.text.toString()

        val intent = Intent()
        intent.putExtra("note", currentNote)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}