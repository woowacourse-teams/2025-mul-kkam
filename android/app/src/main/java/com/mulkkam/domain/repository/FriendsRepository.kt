package com.mulkkam.domain.repository

import com.mulkkam.domain.model.friend.FriendsResult
import com.mulkkam.domain.model.result.MulKkamResult

interface FriendsRepository {
    suspend fun getFriends(
        lastId: Long? = null,
        size: Int,
    ): MulKkamResult<FriendsResult>

    suspend fun getFriendRequestReceivedCount(): MulKkamResult<Int>

    suspend fun deleteFriend(memberId: Long): MulKkamResult<Unit>
}
