package com.mulkkam.domain.model.reminder

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class ReminderSchedule(
    val id: Long,
    val schedule: LocalTime,
)
