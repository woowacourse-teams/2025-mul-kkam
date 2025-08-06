package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.MemberInfo

interface MembersRepository {
    suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit>

    suspend fun patchMembersNickname(nickname: String): MulKkamResult<Unit>

    suspend fun getMembersNickname(): MulKkamResult<String>

    suspend fun getMembers(): MulKkamResult<MemberInfo>
}
