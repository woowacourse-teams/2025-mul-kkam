package com.mulkkam.ui.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import org.koin.core.context.GlobalContext

actual fun openAppNotificationSettings() {
    val context = GlobalContext.get().get<Context>()

    runCatching {
        val intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(intent)
    }.onFailure {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${context.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(intent)
    }
}
