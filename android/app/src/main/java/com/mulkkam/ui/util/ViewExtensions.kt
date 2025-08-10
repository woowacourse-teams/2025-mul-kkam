package com.mulkkam.ui.util

import android.os.SystemClock
import android.view.View

fun View.setSingleClickListener(
    interval: Long = 1000L,
    listener: (View) -> Unit,
) {
    setOnClickListener {
        val currentTime = SystemClock.elapsedRealtime()
        val lastClickTime = (getTag(SINGLE_CLICK_TAG_KEY) as? Long) ?: 0L
        if (currentTime - lastClickTime > interval) {
            setTag(SINGLE_CLICK_TAG_KEY, currentTime)
            listener(it)
        }
    }
}

private const val SINGLE_CLICK_TAG_KEY = -10001
