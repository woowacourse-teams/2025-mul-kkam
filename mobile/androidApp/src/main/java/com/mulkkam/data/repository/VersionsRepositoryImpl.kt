package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.service.VersionsService
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.VersionsRepository

class VersionsRepositoryImpl(
    private val versionsService: VersionsService,
) : VersionsRepository {
    override suspend fun getMinimumVersion(): MulKkamResult<String> {
        val result = versionsService.getMinimumVersion()

        return result.fold(
            onSuccess = { MulKkamResult(data = it.minimumVersion) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
