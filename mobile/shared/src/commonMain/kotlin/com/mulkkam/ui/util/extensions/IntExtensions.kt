package com.mulkkam.ui.util.extensions

import kotlin.math.absoluteValue

fun Int.toCommaSeparated(): String {
    val isNegative: Boolean = this < 0
    val formattedAbsoluteValue: String =
        this.absoluteValue
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    return if (isNegative) "-$formattedAbsoluteValue" else formattedAbsoluteValue
}
