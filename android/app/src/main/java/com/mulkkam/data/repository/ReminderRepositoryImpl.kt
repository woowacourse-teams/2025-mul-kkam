package com.mulkkam.data.repository

import coil3.util.CoilUtils.result
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.reminder.ReminderRequest
import com.mulkkam.data.remote.model.request.reminder.toData
import com.mulkkam.data.remote.model.response.reminder.toDomain
import com.mulkkam.data.remote.service.ReminderService
import com.mulkkam.domain.model.reminder.ReminderConfig
import com.mulkkam.domain.model.reminder.ReminderSchedule
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.ReminderRepository
import java.time.LocalTime
import javax.inject.Inject

class ReminderRepositoryImpl
    @Inject
    constructor(
        private val reminderService: ReminderService,
    ) : ReminderRepository {
        override suspend fun getReminder(): MulKkamResult<ReminderConfig> {
            val result = reminderService.getReminder()

            return result.fold(
                onSuccess = { MulKkamResult(data = it.toDomain()) },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun postReminder(schedule: LocalTime): MulKkamResult<Unit> {
            val result = reminderService.postReminder(ReminderRequest(schedule = schedule.toString()))

            return result.fold(
                onSuccess = { MulKkamResult(data = Unit) },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun patchReminder(reminderSchedule: ReminderSchedule): MulKkamResult<Unit> {
            val result = reminderService.patchReminder(reminderSchedule.toData())

            return result.fold(
                onSuccess = { MulKkamResult(data = Unit) },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun deleteReminder(id: Long): MulKkamResult<Unit> {
            val result = reminderService.deleteReminder(id)

            return result.fold(
                onSuccess = { MulKkamResult(data = Unit) },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }
    }
