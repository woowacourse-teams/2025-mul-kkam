package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.reminder.ReminderRequest
import com.mulkkam.data.remote.model.response.reminder.ReminderResponse

// TODO: DataSource 구현 필요
class ReminderDataSourceImpl : ReminderDataSource {
    override suspend fun getReminder(): Result<ReminderResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun postReminder(reminder: ReminderRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun patchReminder(reminder: ReminderRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminder(id: Long): Result<Unit> {
        TODO("Not yet implemented")
    }
}
