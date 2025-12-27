package com.mulkkam.data.remote.datasource

interface NicknameDataSource {
    suspend fun getNicknameValidation(nickname: String): Result<Unit>
}
