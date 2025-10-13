package com.mulkkam.domain.repository

import com.mulkkam.domain.model.reminder.ReminderConfig
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.domain.model.result.MulKkamResult
import java.time.LocalTime

interface ReminderRepository {
    suspend fun getReminder(): MulKkamResult<ReminderConfig>

    suspend fun postReminder(schedule: LocalTime): MulKkamResult<Unit>

    suspend fun patchReminder(reminder: ReminderSchedule): MulKkamResult<Unit>

    suspend fun deleteReminder(id: Long): MulKkamResult<Unit>
}
