package com.mulkkam.ui.util.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun LocalDate.format(pattern: String): String {
    val dayOfWeekFormatter =
        NSDateFormatter().apply {
            dateFormat = pattern
            locale = NSLocale.currentLocale
        }

    val nsDate =
        this
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toNSDate()

    return dayOfWeekFormatter.stringFromDate(nsDate)
}

@OptIn(ExperimentalTime::class)
actual fun LocalTime.format(pattern: String): String {
    val formatter =
        NSDateFormatter().apply {
            dateFormat = pattern
            locale = NSLocale.currentLocale
        }
    val today =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

    val instant =
        this
            .atDate(today)
            .toInstant(TimeZone.currentSystemDefault())

    val nsDate = instant.toNSDate()

    return formatter.stringFromDate(nsDate)
}

@OptIn(ExperimentalTime::class)
actual fun LocalDateTime.format(pattern: String): String {
    val formatter =
        NSDateFormatter().apply {
            dateFormat = pattern
            locale = NSLocale.currentLocale
        }

    val instant =
        this.toInstant(TimeZone.currentSystemDefault())

    val nsDate = instant.toNSDate()

    return formatter.stringFromDate(nsDate)
}
