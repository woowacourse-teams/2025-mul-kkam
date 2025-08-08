package com.mulkkam.domain.repository

import com.mulkkam.domain.model.MulKkamResult

interface NicknameRepository {
    suspend fun getNicknameValidation(nickname: String): MulKkamResult<Unit>
}
