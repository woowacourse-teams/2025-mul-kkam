package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.repository.MembersRepository

class MembersRepositoryImpl(
    private val membersService: MembersService,
) : MembersRepository {
    override suspend fun getMemberNicknameValidation(nickname: String): MulKkamResult<Unit> {
        val result = membersService.getMemberNicknameValidation(nickname)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
