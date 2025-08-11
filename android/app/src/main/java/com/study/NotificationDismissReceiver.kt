package com.study

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "REJECT_ACTION") {
            val notificationId = intent.getIntExtra("notification_id", -1)
            if (notificationId != -1 && context != null) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(notificationId)
                Log.d("NotificationDismiss", "Notification $notificationId dismissed by Reject")
            }
        }
    }
}

