package com.mulkkam.ui.custom

data class ExtendableFloatingMenuItem<T>(
    val label: String,
    val icon: ExtendableFloatingMenuIcon,
    val data: T? = null,
)
