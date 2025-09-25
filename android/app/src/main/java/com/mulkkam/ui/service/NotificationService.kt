package com.mulkkam.ui.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulkkam.R
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.di.PreferenceInjection.devicesPreference
import com.mulkkam.di.RepositoryInjection.devicesRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class NotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                tokenRepository.saveFcmToken(token)
                devicesRepository.postDevice(token)
            }.onFailure {
                mulKkamLogger.error(
                    LogEvent.ERROR,
                    "FCM Token Save Failed: ${it::class.java.simpleName}: ${it.message}\n${it.stackTraceToString()}",
                )
                devicesPreference.saveNotificationGranted(!devicesPreference.isNotificationGranted)
            }
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
        val notificationId = UUID.randomUUID().hashCode() xor System.currentTimeMillis().hashCode()
        val pendingIntent = createPendingIntent(action, notificationId)

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

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
        notificationManager.notify(notificationId, notification)
    }

    private fun createPendingIntent(
        action: String,
        notificationId: Int,
    ): PendingIntent {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_ACTION, action)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        return PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val CHANNEL_ID: String = "MULKKAM_NOTIFICATION_CHANNEL"
        const val CHANNEL_NAME: String = "MulKkam Notification"
        const val CHANNEL_DESC: String = "MulKkam Default Notification Channel"

        const val EXTRA_ACTION: String = "EXTRA_ACTION"
        const val EXTRA_NOTIFICATION_ID: String = "EXTRA_NOTIFICATION_ID"

        private const val KEY_GROUP: String = "MULKKAM_GROUP"
        private const val TOPIC_DEFAULT: String = "mulkkam"
    }
}
