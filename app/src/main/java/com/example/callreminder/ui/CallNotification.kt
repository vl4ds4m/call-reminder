package com.example.callreminder.ui

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.callreminder.R

const val channelID = "CallReminderMainChannel"
const val idExtra = "CallNotificationID"
const val messageExtra = "CallNotificationMessage"

class CallNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(messageExtra))
            .setContentText(context.getString(R.string.notification_content_text))
            .build()

        val noteID = intent.getIntExtra(idExtra, 0)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(noteID, notification)
    }
}