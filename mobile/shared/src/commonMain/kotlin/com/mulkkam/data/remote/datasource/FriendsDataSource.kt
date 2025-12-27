package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.FriendWaterBalloonRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import com.mulkkam.data.remote.model.response.friends.FriendRequestReceivedCountResponse
import com.mulkkam.data.remote.model.response.friends.FriendsRequestResponse
import com.mulkkam.data.remote.model.response.friends.FriendsResponse

interface FriendsDataSource {
    suspend fun getFriends(
        lastId: Long? = null,
        size: Int,
    ): Result<FriendsResponse>

    suspend fun getFriendRequestReceivedCount(): Result<FriendRequestReceivedCountResponse>

    suspend fun deleteFriend(memberId: Long): Result<Unit>

    suspend fun postFriendRequest(friendRequest: FriendRequest): Result<Unit>

    suspend fun postFriendWaterBalloon(friendWaterBalloonRequest: FriendWaterBalloonRequest): Result<Unit>

    suspend fun patchFriendRequests(patchFriendRequest: PatchFriendRequest): Result<Unit>

    suspend fun getFriendsRequestsSent(
        lastId: Long?,
        size: Int,
    ): Result<FriendsRequestResponse>

    suspend fun getFriendsRequestsReceived(
        lastId: Long?,
        size: Int,
    ): Result<FriendsRequestResponse>

    suspend fun deleteFriendsRequest(memberId: Long): Result<Unit>
}
