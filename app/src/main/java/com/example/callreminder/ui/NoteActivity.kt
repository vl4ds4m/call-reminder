package com.example.callreminder.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.callreminder.R
import com.example.callreminder.Note
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

class NoteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescr: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescr = findViewById(R.id.editTextDescr)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)

        val isNewNote = intent.getBooleanExtra(isNewNoteExtra, false)

        if (isNewNote) {
            editTextTitle.setText("")
            editTextDescr.setText("")
            editTextPhone.setText("")
            editTextDate.setText("")
            editTextTime.setText("")
        } else {
            val note = intent.getSerializableExtra(noteExtra) as Note
            editTextTitle.setText(note.title)
            editTextDescr.setText(note.description)
            editTextPhone.setText(note.phone)
            editTextDate.setText(note.time.split(" ")[0])
            editTextTime.setText(note.time.split(" ")[1])
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.save_button -> {
                fillNote()
            }
        }
    }

    private fun fillNote() {
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

        val newNote = Note()
        newNote.title = editTextTitle.text.toString()
        newNote.description = editTextDescr.text.toString()
        newNote.phone = editTextPhone.text.toString()
        newNote.time = editTextDate.text.toString() + " " + editTextTime.text.toString()

        val intent = Intent()
        intent.putExtra(noteExtra, newNote)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}