package com.mulkkam.domain.repository

import com.mulkkam.domain.MulKkamResult

interface MembersRepository {
    suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit>
}
