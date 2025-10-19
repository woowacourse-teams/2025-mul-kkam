package com.mulkkam.domain.repository

import com.mulkkam.domain.model.result.MulKkamResult

interface FriendsRepository {
    suspend fun getFriends(): MulKkamResult<Unit>
}
