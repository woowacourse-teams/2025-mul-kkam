package com.mulkkam.data.remote.model.response.reminder

import com.mulkkam.domain.model.reminder.ReminderConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReminderResponse(
    @SerialName("isReminderEnabled")
    val isReminderEnabled: Boolean,
    @SerialName("reminderSchedules")
    val reminderSchedules: List<ReminderScheduleResponse>?,
)

fun ReminderResponse.toDomain(): ReminderConfig =
    ReminderConfig(
        isReminderEnabled = isReminderEnabled,
        reminderSchedules = reminderSchedules?.map { it.toDomain() },
    )
