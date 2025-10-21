package com.mulkkam.ui.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mulkkam.R
import com.mulkkam.data.local.preference.DevicesPreference
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService
    @Inject
    constructor(
        private val tokenRepository: TokenRepository,
        private val devicesRepository: DevicesRepository,
        private val devicesPreference: DevicesPreference,
        private val logger: Logger,
    ) : FirebaseMessagingService() {
        override fun onNewToken(token: String) {
            super.onNewToken(token)
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    tokenRepository.saveFcmToken(token)
                    devicesRepository.postDevice(token)
                }.onSuccess {
                    logger.info(LogEvent.PUSH_NOTIFICATION, "FCM Token Saved")
                }.onFailure {
                    logger.error(
                        LogEvent.PUSH_NOTIFICATION,
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

            val title =
                remoteMessage.data["title"] ?: remoteMessage.notification?.title
                    ?: getString(R.string.app_name)
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

            logger.info(
                LogEvent.PUSH_NOTIFICATION,
                "Displaying notification (id=$notificationId) with action=$action",
            )

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

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
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
