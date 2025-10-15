package com.mulkkam.domain.model.reminder

import java.time.LocalTime

data class ReminderSchedule(
    val id: Long,
    val schedule: LocalTime,
)
