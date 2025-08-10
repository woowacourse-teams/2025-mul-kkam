package com.mulkkam.ui.custom

data class ExtendableFloatingMenuItem<T>(
    val buttonLabel: String,
    val icon: ExtendableFloatingMenuIcon,
    val iconLabel: String? = null,
    val data: T? = null,
)
