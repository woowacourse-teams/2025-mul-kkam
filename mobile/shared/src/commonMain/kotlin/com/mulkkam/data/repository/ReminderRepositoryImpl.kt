package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.ReminderRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.reminder.ReminderRequest
import com.mulkkam.data.remote.model.request.reminder.toData
import com.mulkkam.data.remote.model.response.reminder.toDomain
import com.mulkkam.domain.model.reminder.ReminderConfig
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.ReminderRepository
import kotlinx.datetime.LocalTime

class ReminderRepositoryImpl(
    private val reminderRemoteDataSource: ReminderRemoteDataSource,
) : ReminderRepository {
    override suspend fun getReminder(): MulKkamResult<ReminderConfig> {
        val result = reminderRemoteDataSource.getReminder()

        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postReminder(schedule: LocalTime): MulKkamResult<Unit> {
        val result = reminderRemoteDataSource.postReminder(ReminderRequest(schedule = schedule.toString()))

        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun patchReminder(reminderSchedule: ReminderSchedule): MulKkamResult<Unit> {
        val result = reminderRemoteDataSource.patchReminder(reminderSchedule.toData())

        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteReminder(id: Long): MulKkamResult<Unit> {
        val result = reminderRemoteDataSource.deleteReminder(id)

        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
