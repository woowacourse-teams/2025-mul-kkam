package com.mulkkam.ui.custom

import androidx.annotation.DrawableRes

sealed class ExtendableFloatingMenuIcon {
    data class Url(
        val url: String,
    ) : ExtendableFloatingMenuIcon()

    data class Resource(
        @DrawableRes val resId: Int,
    ) : ExtendableFloatingMenuIcon()
}
