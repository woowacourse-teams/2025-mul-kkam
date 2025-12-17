package com.mulkkam.domain.model.reminder

data class ReminderConfig(
    val isReminderEnabled: Boolean,
    val reminderSchedules: List<ReminderSchedule>?,
)
