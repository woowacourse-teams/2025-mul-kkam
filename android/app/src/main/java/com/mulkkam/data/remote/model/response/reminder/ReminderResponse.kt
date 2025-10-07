package com.mulkkam.data.remote.model.response.reminder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReminderResponse(
    @SerialName("isReminderEnabled")
    val isReminderEnabled: Boolean,
    @SerialName("reminderSchedules")
    val reminderSchedules: List<ReminderScheduleResponse>?,
)
