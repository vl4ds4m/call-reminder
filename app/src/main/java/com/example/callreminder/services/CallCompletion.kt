package com.example.callreminder.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.callreminder.Note
import com.example.callreminder.db.AppDB
import com.example.callreminder.db.getDB
import com.example.callreminder.ui.noteExtra

class CallCompletion : BroadcastReceiver() {
    private lateinit var notesDB: AppDB

    override fun onReceive(context: Context, intent: Intent) {
        val note = intent.getSerializableExtra(noteExtra) as Note

        notesDB = getDB(context)
        notesDB.getDAO().delete(note)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(note.id)
    }
}