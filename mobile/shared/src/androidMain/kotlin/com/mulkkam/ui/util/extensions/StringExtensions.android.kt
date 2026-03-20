package com.mulkkam.ui.util.extensions

import android.content.Context
import android.content.Intent
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import org.koin.core.context.GlobalContext

actual fun String.toColorInt(): Int = this.toColorInt()

actual fun String.openLink() {
    val context: Context = GlobalContext.get().get()

    val uri = toUri()
    val scheme: String = uri.scheme?.lowercase() ?: return

    val intent: Intent =
        when (scheme) {
            "mailto" -> Intent(Intent.ACTION_SENDTO, uri)
            "http", "https" -> Intent(Intent.ACTION_VIEW, uri)
            else -> return
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}
