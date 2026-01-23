package com.mulkkam.ui.util.extensions

import android.content.Context
import android.content.Intent
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import org.koin.core.context.GlobalContext

actual fun String.toColorInt(): Int = this.toColorInt()

actual fun String.openLink() {
    val context: Context = GlobalContext.get().get()
    val intent =
        Intent(
            Intent.ACTION_VIEW,
            this.toUri(),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    if (context.canHandleIntent(intent)) {
        context.startActivity(intent)
    }
}
