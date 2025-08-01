package com.mulkkam.ui.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulkkam.R
import com.mulkkam.ui.main.MainActivity

class NotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token: $token")
        subscribeToTopic(DEFAULT_TOPIC)
    }

    private fun subscribeToTopic(topic: String) {
        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to topic: $topic")
                } else {
                    Log.w("FCM", "Failed to subscribe to topic: $topic", task.exception)
                }
            }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: getString(R.string.app_name)
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""
        val target = remoteMessage.data["target"] ?: TARGET_HOME
        val payload = remoteMessage.data["payload"] ?: ""

        showNotification(title, body, target, payload)
    }

    private fun showNotification(
        title: String,
        body: String,
        target: String,
        payload: String,
    ) {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_TARGET, target)
                putExtra(EXTRA_PAYLOAD, payload)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val builder =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    companion object {
        const val CHANNEL_ID = "MULKKAM_NOTIFICATION_CHANNEL"
        const val CHANNEL_NAME = "Mulkkam Notification"
        const val CHANNEL_DESC = "Mulkkam Default Notification Channel"

        const val GROUP_KEY = "MULKKAM_GROUP"

        const val EXTRA_TARGET = "extra_target"
        const val EXTRA_PAYLOAD = "extra_payload"

        const val TARGET_HOME = "HOME"

        private const val DEFAULT_TOPIC = "mulkkam"
    }
}
