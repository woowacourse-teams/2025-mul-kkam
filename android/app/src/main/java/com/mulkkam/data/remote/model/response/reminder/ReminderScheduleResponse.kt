package com.mulkkam.data.remote.model.response.reminder

import com.mulkkam.domain.model.reminder.ReminderSchedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class ReminderScheduleResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("schedule")
    val schedule: String,
)

fun ReminderScheduleResponse.toDomain(): ReminderSchedule =
    ReminderSchedule(
        id = id,
        schedule = LocalTime.parse(this@toDomain.schedule),
    )
