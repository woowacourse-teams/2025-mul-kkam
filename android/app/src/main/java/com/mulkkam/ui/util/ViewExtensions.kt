package com.mulkkam.ui.util

import android.view.View

fun View.setSingleClickListener(
    interval: Long = 1000L,
    listener: (View) -> Unit,
) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > interval) {
            lastClickTime = currentTime
            listener(it)
        }
    }
}
