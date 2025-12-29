package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.NicknameRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.NicknameRepository

class NicknameRepositoryImpl(
    private val nicknameRemoteDataSource: NicknameRemoteDataSource,
) : NicknameRepository {
    override suspend fun getNicknameValidation(nickname: String): MulKkamResult<Unit> {
        val result = nicknameRemoteDataSource.getNicknameValidation(nickname)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
