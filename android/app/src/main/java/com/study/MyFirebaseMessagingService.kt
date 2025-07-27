// package com.study

// import android.app.NotificationChannel
// import android.app.NotificationManager
// import android.app.PendingIntent
// import android.content.Context
// import android.content.Intent
// import android.media.AudioAttributes
// import android.net.Uri
// import android.os.Build
// import android.util.Log
// import androidx.core.app.NotificationCompat
// import com.google.firebase.messaging.FirebaseMessagingService
// import com.google.firebase.messaging.RemoteMessage

// class MyFirebaseMessagingService : FirebaseMessagingService() {

//     companion object {
//         private const val TAG = "MyFirebaseMsgService"
//         private const val CHANNEL_ID = "default_channel"
//     }

//     override fun onMessageReceived(remoteMessage: RemoteMessage) {
//         Log.d(TAG, "From: ${remoteMessage.from}")

//         val data = remoteMessage.data
//         val title = remoteMessage.notification?.title ?: data["title"] ?: "New Message"
//         val body = remoteMessage.notification?.body ?: data["body"] ?: ""
//         val actions = remoteMessage.data ?: data["actions"] ?: "Add,Not" 

//         sendNotification(title, body, actions)
//     }

//     override fun onNewToken(token: String) {
//         Log.d(TAG, "Refreshed token: $token")
//         // Send token to your backend server if needed
//     }

//     private fun sendNotification(title: String, messageBody: String, actions: String?) {
//         val mainIntent = Intent(this, MainActivity::class.java).apply {
//             flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//             action = "OPEN_ACTIVITY"
//         }

//         val pendingIntent = PendingIntent.getActivity(
//             this,
//             0,
//             mainIntent,
//             PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//         )

//         val soundUri = Uri.parse("android.resource://$packageName/raw/sound")

//         val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//             .setSmallIcon(R.mipmap.ic_launcher)
//             .setContentTitle(title)
//             .setContentText(messageBody)
//             .setAutoCancel(true)
//             .setSound(soundUri)
//             .setContentIntent(pendingIntent)
//             .setPriority(NotificationCompat.PRIORITY_HIGH)

//         // Dynamically add actions if present
//         actions?.split(",")?.forEachIndexed { index, actionLabel ->
//             val actionIntent = Intent(this, MainActivity::class.java).apply {
//                 flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                 action = actionLabel.trim()
//             }

//             val actionPendingIntent = PendingIntent.getActivity(
//                 this,
//                 index + 1,
//                 actionIntent,
//                 PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//             )

//             builder.addAction(0, actionLabel.trim(), actionPendingIntent) // icon = 0 means no icon
//         }

//         val notificationManager =
//             getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//             val audioAttributes = AudioAttributes.Builder()
//                 .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                 .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                 .build()

//             val channel = NotificationChannel(
//                 CHANNEL_ID,
//                 "Default Channel",
//                 NotificationManager.IMPORTANCE_HIGH
//             ).apply {
//                 setSound(soundUri, audioAttributes)
//             }

//             notificationManager.createNotificationChannel(channel)
//         }

//         notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
//     }
// }

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
        val actions = data["actions"]  // Only this, not remoteMessage.data again

        sendNotification(title, body, actions)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // Send token to your backend if needed
    }

    private fun sendNotification(title: String, messageBody: String, actions: String?) {
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
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // 🔘 Add action buttons (if provided)
        actions?.split(",")?.forEachIndexed { index, actionLabel ->
            val trimmedLabel = actionLabel.trim()
            if (trimmedLabel.isNotEmpty()) {
                val actionIntent = Intent(this, MainActivity::class.java).apply {
                    action = trimmedLabel
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val actionPendingIntent = PendingIntent.getActivity(
                    this,
                    index + 1,
                    actionIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )

                builder.addAction(0, trimmedLabel, actionPendingIntent) // 0 = no icon
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 🔔 Create Notification Channel (if needed)
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

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
