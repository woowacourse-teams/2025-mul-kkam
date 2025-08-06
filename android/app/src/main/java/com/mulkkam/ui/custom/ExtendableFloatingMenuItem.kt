package com.mulkkam.ui.custom

data class ExtendableFloatingMenuItem<T>(
    val label: String,
    val iconUrl: String,
    val data: T? = null,
)
