package com.mulkkam.ui.util

import android.os.SystemClock
import android.view.View

fun View.setSingleClickListener(
    interval: Long = 1000L,
    listener: (View) -> Unit,
) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime > interval) {
            lastClickTime = currentTime
            listener(it)
        }
    }
}
