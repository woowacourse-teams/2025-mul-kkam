package com.mulkkam.ui.util

sealed class ImageShape {
    data object None : ImageShape()

    data object Circle : ImageShape()

    data class Rounded(
        val radiusDp: Int,
    ) : ImageShape()

    data class RoundedCorners(
        val topLeftDp: Int,
        val topRightDp: Int,
        val bottomRightDp: Int,
        val bottomLeftDp: Int,
    ) : ImageShape()
}
