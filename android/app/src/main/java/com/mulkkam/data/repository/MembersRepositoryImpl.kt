package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.MemberNicknameRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.MemberInfo
import com.mulkkam.domain.repository.MembersRepository

class MembersRepositoryImpl(
    private val membersService: MembersService,
) : MembersRepository {
    override suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit> {
        val result = membersService.getMembersNicknameValidation(nickname)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun patchMembersNickname(nickname: String): MulKkamResult<Unit> {
        val result = membersService.patchMembersNickname(MemberNicknameRequest(nickname))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getMembersNickname(): MulKkamResult<String> {
        val result = membersService.getMembersNickname()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.memberNickname) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getMembers(): MulKkamResult<MemberInfo> {
        val result = membersService.getMembers(1)
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
