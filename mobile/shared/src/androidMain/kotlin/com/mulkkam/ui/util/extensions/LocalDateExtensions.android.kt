package com.mulkkam.ui.util.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

actual fun LocalDate.format(pattern: String): String {
    val formatter =
        DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

    return this
        .toJavaLocalDate()
        .format(formatter)
}

actual fun LocalTime.format(pattern: String): String {
    val timeFormatterWithoutMinutes = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return this.toJavaLocalTime().format(timeFormatterWithoutMinutes)
}
