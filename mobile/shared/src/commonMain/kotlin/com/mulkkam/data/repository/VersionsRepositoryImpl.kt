package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.VersionRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.VersionsRepository

class VersionsRepositoryImpl(
    private val versionsRemoteDataSource: VersionRemoteDataSource,
) : VersionsRepository {
    override suspend fun getMinimumVersion(): MulKkamResult<String> {
        val result = versionsRemoteDataSource.getMinimumVersion()

        return result.fold(
            onSuccess = { MulKkamResult(data = it.minimumVersion) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
