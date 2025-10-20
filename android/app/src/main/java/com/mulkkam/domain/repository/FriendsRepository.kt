package com.mulkkam.domain.repository

import com.mulkkam.domain.model.friends.FriendsRequestResult
import com.mulkkam.domain.model.friends.FriendsStatus
import com.mulkkam.domain.model.result.MulKkamResult

interface FriendsRepository {
    suspend fun getFriends(): MulKkamResult<Unit>

    suspend fun getFriendsRequestReceived(
        lastId: Long? = null,
        size: Int,
    ): MulKkamResult<FriendsRequestResult>

    suspend fun getFriendsRequestSent(
        lastId: Long? = null,
        size: Int,
    ): MulKkamResult<FriendsRequestResult>

    suspend fun patchFriendsRequest(
        requestId: Long,
        status: FriendsStatus,
    ): MulKkamResult<Unit>

    suspend fun deleteFriendsRequest(requestId: Long): MulKkamResult<Unit>
}
