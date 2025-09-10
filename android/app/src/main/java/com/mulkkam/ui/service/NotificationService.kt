package com.mulkkam.ui.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulkkam.R
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            tokenRepository.saveFcmToken(token)
        }
        subscribeToTopic()
    }

    private fun subscribeToTopic() {
        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(TOPIC_DEFAULT)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: getString(R.string.app_name)
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""
        val action = remoteMessage.data["action"] ?: NotificationAction.UNKNOWN.name

        showNotification(title, body, action)
    }

    private fun showNotification(
        title: String,
        body: String,
        action: String,
    ) {
        val pendingIntent = createPendingIntent(action)

        val notification =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(KEY_GROUP)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_ACTION, action)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val CHANNEL_ID = "MULKKAM_NOTIFICATION_CHANNEL"
        const val CHANNEL_NAME = "MulKkam Notification"
        const val CHANNEL_DESC = "MulKkam Default Notification Channel"

        const val EXTRA_ACTION = "EXTRA_ACTION"

        private const val KEY_GROUP = "MULKKAM_GROUP"
        private const val TOPIC_DEFAULT = "mulkkam"
    }
}
