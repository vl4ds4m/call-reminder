package com.example.callreminder.ui

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.callreminder.R

const val channelID = "CallReminderMainChannel"
const val idExtra = "CallNotificationID"
const val titleExtra = "CallNotificationTitle"
const val textExtra = "CallNotificationText"
const val phoneExtra = "CallNotificationPhone"

class CallNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val callIntent = Intent()
        callIntent.action = Intent.ACTION_DIAL
        callIntent.data = Uri.parse("tel:" + intent.getStringExtra(phoneExtra))
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            callIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(textExtra))
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_call,
                    "Call",
                    pendingIntent
                )
            ).build()

        val noteID = intent.getIntExtra(idExtra, 0)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(noteID, notification)
    }
}