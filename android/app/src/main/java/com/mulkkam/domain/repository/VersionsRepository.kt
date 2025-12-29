package com.mulkkam.domain.repository

import com.mulkkam.domain.model.result.MulKkamResult

interface VersionsRepository {
    suspend fun getMinimumVersion(): MulKkamResult<String>
}
