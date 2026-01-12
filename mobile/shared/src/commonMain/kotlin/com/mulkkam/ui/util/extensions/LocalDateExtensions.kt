package com.mulkkam.ui.util.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

expect fun LocalDate.format(pattern: String): String

expect fun LocalTime.format(pattern: String): String
