package com.mulkkam.domain.model.reminder

import kotlinx.serialization.Serializable

@Serializable
data class ReminderConfig(
    val isReminderEnabled: Boolean,
    val reminderSchedules: List<ReminderSchedule>?,
)
