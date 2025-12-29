package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.reminder.ReminderRequest
import com.mulkkam.data.remote.model.response.reminder.ReminderResponse

interface ReminderDataSource {
    suspend fun getReminder(): Result<ReminderResponse>

    suspend fun postReminder(reminder: ReminderRequest): Result<Unit>

    suspend fun patchReminder(reminder: ReminderRequest): Result<Unit>

    suspend fun deleteReminder(id: Long): Result<Unit>
}
