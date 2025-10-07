package com.mulkkam.data.remote.model.response.reminder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReminderScheduleResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("schedule")
    val schedule: String,
)
