package com.mulkkam.data.remote.model.request.reminder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReminderRequest(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("schedule")
    val schedule: String,
)
