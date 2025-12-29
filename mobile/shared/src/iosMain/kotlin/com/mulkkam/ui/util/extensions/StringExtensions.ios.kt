package com.mulkkam.ui.util.extensions

actual fun String.toColorInt(): Int {
    val hex = removePrefix("#")
    val rgb = hex.toInt(16)
    return (0xFF shl 24) or rgb
}
