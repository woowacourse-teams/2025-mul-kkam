package com.mulkkam.domain.repository

import com.mulkkam.domain.model.members.Status
import com.mulkkam.domain.model.result.MulKkamResult

interface FriendsRepository {
    suspend fun getFriends(): MulKkamResult<Unit>

    suspend fun postFriendRequest(id: Long): MulKkamResult<Unit>

    suspend fun patchFriendRequest(
        requestId: Long,
        status: Status,
    ): MulKkamResult<Unit>
}
