package com.mulkkam.domain.repository

import com.mulkkam.domain.model.friends.FriendsRequestResult
import com.mulkkam.domain.model.members.Status
import com.mulkkam.domain.model.result.MulKkamResult

interface FriendsRepository {
    suspend fun getFriends(): MulKkamResult<Unit>

    suspend fun postFriendRequest(id: Long): MulKkamResult<Unit>

    suspend fun patchFriendRequest(
        requestId: Long,
        status: Status,
    ): MulKkamResult<Unit>

    suspend fun getFriendsRequestReceived(
        lastId: Long? = null,
        size: Int,
    ): MulKkamResult<FriendsRequestResult>

    suspend fun getFriendsRequestSent(
        lastId: Long? = null,
        size: Int,
    ): MulKkamResult<FriendsRequestResult>

    suspend fun deleteFriendsRequest(requestId: Long): MulKkamResult<Unit>
}
