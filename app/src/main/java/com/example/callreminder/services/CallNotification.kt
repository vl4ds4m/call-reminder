package com.example.callreminder.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.callreminder.R
import com.example.callreminder.Note
import com.example.callreminder.ui.NOTE_EXTRA

const val channelID = "CallReminderMainChannel"

class CallNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val note = intent.getSerializableExtra(NOTE_EXTRA) as Note

        val notificationBuilder = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(note.title)
            .setContentText(note.description)

        notificationBuilder.addAction(
            R.drawable.ic_call,
            context.getString(R.string.make_call_text),
            createCallIntent(context, note)
        )
        notificationBuilder.addAction(
            R.drawable.ic_check,
            context.getString(R.string.complete_call_text),
            createCompletionIntent(context, note)
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(note.id, notificationBuilder.build())
    }

    private fun createCallIntent(context: Context, note: Note): PendingIntent {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + note.phone))
        return PendingIntent.getActivity(
            context,
            note.id,
            callIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createCompletionIntent(context: Context, note: Note): PendingIntent {
        val completionIntent = Intent(context, CallCompletion::class.java)
        completionIntent.putExtra(NOTE_EXTRA, note)
        return PendingIntent.getBroadcast(
            context,
            note.id,
            completionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}