package com.mulkkam.data.remote.model.request.reminder

import com.mulkkam.domain.model.reminder.ReminderSchedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReminderRequest(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("schedule")
    val schedule: String,
)

fun ReminderSchedule.toData(): ReminderRequest =
    ReminderRequest(
        id = id,
        schedule = schedule.toString(),
    )
