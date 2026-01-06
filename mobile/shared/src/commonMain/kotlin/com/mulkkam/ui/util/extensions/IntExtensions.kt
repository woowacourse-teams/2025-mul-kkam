package com.mulkkam.ui.util.extensions

fun Int.toCommaSeparated(): String =
    this
        .toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
