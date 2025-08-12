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
import com.facebook.react.ReactApplication
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tencent.mmkv.MMKV
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "default_channel"
        private const val NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_MAX
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Process message in background thread for better performance
        Thread {
            try {
                val data = remoteMessage.data
                val title = data["title"] ?: remoteMessage.notification?.title ?: "New Message"
                val body = data["body"] ?: remoteMessage.notification?.body ?: ""
                val actions = data["actions"]
                val autoApprove = data["modelKey"]?.startsWith("approve_", ignoreCase = true) == true


                // Initialize MMKV (do this once per process)
MMKV.initialize(applicationContext)

// Get default MMKV instance
val mmkv = MMKV.defaultMMKV()

// Store notification JSON
val notifJson = JSONObject().apply {
    put("id", System.currentTimeMillis().toString())
    put("title", title)
    put("body", body)
    put("data", JSONObject(data))
    put("receivedAt", System.currentTimeMillis())
}.toString()

// Save in a list
val existing = mmkv.decodeStringSet("notifications")?.toMutableSet() ?: mutableSetOf()
existing.add(notifJson)
mmkv.encode("notifications", remoteMessage)

Log.d(TAG, "📦 Notification saved in MMKV: $notifJson")


                // Immediately send to React Native before showing notification
                sendNotificationToReactNative(title, body, data)

                // Show system notification
                sendNotification(title, body, actions, data)

                // Handle auto-approve if needed
                if (autoApprove) {
                    triggerApproveAction(data)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
        }.start()
    }

    private fun sendNotificationToReactNative(title: String, body: String, data: Map<String, String>) {
        try {
            val notificationData = createNotificationMap(title, body, data)
            
            // Send to both React Native and broadcast for other parts of the app
            sendEventToReactNative("FCMNotificationReceived", notificationData)
            sendLocalBroadcast(notificationData)
            
            Log.d(TAG, "Real-time notification sent: $title")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send real-time notification", e)
        }
    }

    private fun createNotificationMap(title: String, body: String, data: Map<String, String>): WritableMap {
        return Arguments.createMap().apply {
            putString("id", System.currentTimeMillis().toString())
            putString("title", title)
            putString("body", body)
            putString("receivedAt", System.currentTimeMillis().toString())
            
            val dataMap = Arguments.createMap()
            data.forEach { (key, value) -> dataMap.putString(key, value) }
            putMap("data", dataMap)
        }
    }

    private fun sendEventToReactNative(eventName: String, params: WritableMap?) {
        try {
            val reactApp = application as ReactApplication
            reactApp.reactNativeHost.reactInstanceManager.currentReactContext?.let { reactContext ->
                if (reactContext.hasActiveCatalystInstance()) {
                    reactContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                        .emit(eventName, params)
                    Log.d(TAG, "React Native event emitted: $eventName")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "React Native event emission failed", e)
        }
    }

    private fun sendLocalBroadcast(notificationData: WritableMap) {
        try {
            val intent = Intent("com.study.NOTIFICATION_RECEIVED").apply {
                putExtra("notification", notificationData.toHashMap())
            }
            sendBroadcast(intent)
            Log.d(TAG, "Local broadcast sent")
        } catch (e: Exception) {
            Log.e(TAG, "Local broadcast failed", e)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // Implement your token refresh logic here
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        actions: String?,
        data: Map<String, String>
    ) {
        val notificationId = System.currentTimeMillis().toInt()
        val soundUri = Uri.parse("android.resource://$packageName/raw/sound")

        // Create notification channel for Android O+
        createNotificationChannel(soundUri)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(createContentIntent())
            .setPriority(NOTIFICATION_PRIORITY)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Add actions if available
        actions?.let { addNotificationActions(notificationBuilder, it, notificationId, data) }

        // Show notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel(soundUri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Default notifications"
                setSound(soundUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                )
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun addNotificationActions(
        builder: NotificationCompat.Builder,
        actions: String,
        notificationId: Int,
        data: Map<String, String>
    ) {
        actions.split(",").forEachIndexed { index, actionLabel ->
            when (actionLabel.trim().lowercase()) {
                "approve" -> {
                    val intent = Intent(this, BottomSheetActivity::class.java).apply {
                        putExtra("notification_id", notificationId)
                        putExtra("payload", data.toString())
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        this,
                        1000 + index,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    builder.addAction(R.drawable.ic_approve, "Approve", pendingIntent)
                }
                "reject" -> {
                    val intent = Intent(this, NotificationDismissReceiver::class.java).apply {
                        action = "REJECT_ACTION"
                        putExtra("notification_id", notificationId)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        this,
                        2000 + index,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    builder.addAction(0, "Reject", pendingIntent)
                }
            }
        }
    }

    private fun triggerApproveAction(data: Map<String, String>) {
        try {
            val intent = Intent(this, BottomSheetActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("payload", data.toString())
                putExtra("auto_approve", true)
            }
            startActivity(intent)
            Log.d(TAG, "Auto-approve activity started")
        } catch (e: Exception) {
            Log.e(TAG, "Auto-approve failed", e)
        }
    }
}