package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult

interface MembersRepository {
    suspend fun getMemberNicknameValidation(nickname: String): MulKkamResult<Unit>
}
