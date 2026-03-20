package com.mulkkam.data.remote.datasource

interface NicknameRemoteDataSource {
    suspend fun getNicknameValidation(nickname: String): Result<Unit>
}
