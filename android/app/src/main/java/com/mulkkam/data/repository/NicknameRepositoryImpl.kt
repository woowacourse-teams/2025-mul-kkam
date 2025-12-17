package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.service.NicknameService
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.NicknameRepository

class NicknameRepositoryImpl(
    private val nicknameService: NicknameService,
) : NicknameRepository {
    override suspend fun getNicknameValidation(nickname: String): MulKkamResult<Unit> {
        val result = nicknameService.getNicknameValidation(nickname)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
