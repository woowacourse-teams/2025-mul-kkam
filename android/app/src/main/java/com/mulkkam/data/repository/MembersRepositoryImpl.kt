package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.MemberNicknameRequest
import com.mulkkam.data.remote.model.request.MembersPhysicalAtrributesRequest
import com.mulkkam.data.remote.model.response.toDomain
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.domain.Gender
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
        // TODO: 서버 경로 변경 시 인자 제거
        val result = membersService.getMembers(1)
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun postMembersPhysicalAttributes(
        gender: Gender,
        weight: Int,
    ): MulKkamResult<Unit> {
        val result =
            membersService.postMembersPhysicalAttributes(
                MembersPhysicalAtrributesRequest(
                    gender.name,
                    weight.toDouble(),
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getMembersCheckOnboarding(): MulKkamResult<Boolean> {
        val result = membersService.getMembersCheckOnboarding()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.finishedOnboarding) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
