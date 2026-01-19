package com.mulkkam.ui.util.extensions

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.openTermsLink(uri: String) {
    val intent =
        Intent(
            Intent.ACTION_VIEW,
            uri.toUri(),
        )
    startActivity(intent)
}
