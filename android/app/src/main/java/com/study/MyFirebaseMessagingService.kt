package com.study

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "default_channel"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        val data = remoteMessage.data
        val title = data["title"] ?: remoteMessage.notification?.title ?: "New Message"
        val body = data["body"] ?: remoteMessage.notification?.body ?: ""
        val actions = data["actions"]
        val autoApprove = data["modelKey"]?.startsWith("approve_", ignoreCase = true) == true

        // Show the notification as usual
        sendNotification(title, body, actions, data)

        // If backend says auto approve → trigger immediately
        if (autoApprove) {
            triggerApproveAction(data)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // Send token to your backend if needed
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        actions: String?,
        data: Map<String, String>
    ) {
        val notificationId = System.currentTimeMillis().toInt()

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = "OPEN_ACTIVITY"
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = Uri.parse("android.resource://$packageName/raw/sound")

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        actions?.split(",")?.forEachIndexed { index, actionLabel ->
            val trimmedLabel = actionLabel.trim()
            if (trimmedLabel.equals("Approve", ignoreCase = true)) {
                val approveIntent = Intent(this, BottomSheetActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("notification_id", notificationId)
                    putExtra("payload", data.toString())
                }

                val approvePendingIntent = PendingIntent.getActivity(
                    this,
                    1000 + index,
                    approveIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )

                builder.addAction(R.drawable.ic_approve, trimmedLabel, approvePendingIntent)
            } else if (trimmedLabel.equals("Reject", ignoreCase = true)) {
                val rejectIntent = Intent(this, NotificationDismissReceiver::class.java).apply {
                    action = "REJECT_ACTION"
                    putExtra("notification_id", notificationId)
                }

                val rejectPendingIntent = PendingIntent.getBroadcast(
                    this,
                    index + 2,
                    rejectIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )

                builder.addAction(0, trimmedLabel, rejectPendingIntent)
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
            }

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Auto-approve logic
     */
    private fun triggerApproveAction(data: Map<String, String>) {
        try {
            val approveIntent = Intent(this, BottomSheetActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("payload", data.toString())
            }
            startActivity(approveIntent)
            Log.d(TAG, "Auto-approve triggered immediately")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to auto-approve", e)
        }
    }
}
