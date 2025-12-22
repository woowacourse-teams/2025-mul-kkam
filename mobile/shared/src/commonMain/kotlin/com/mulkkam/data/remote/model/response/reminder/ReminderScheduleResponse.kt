package com.mulkkam.data.remote.model.response.reminder

import com.mulkkam.domain.model.reminder.ReminderSchedule
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReminderScheduleResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("schedule")
    val schedule: String,
)

fun ReminderScheduleResponse.toDomain(): ReminderSchedule {
    val parts = schedule.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    val second = parts.getOrNull(2)?.toInt() ?: 0

    return ReminderSchedule(
        id = id,
        schedule = LocalTime(hour, minute, second),
    )
}
