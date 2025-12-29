package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.FriendWaterBalloonRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import com.mulkkam.data.remote.model.response.friends.FriendRequestReceivedCountResponse
import com.mulkkam.data.remote.model.response.friends.FriendsRequestResponse
import com.mulkkam.data.remote.model.response.friends.FriendsResponse

// TODO: DataSource 구현 필요
class FriendsDataSourceImpl : FriendsDataSource {
    override suspend fun getFriends(
        lastId: Long?,
        size: Int,
    ): Result<FriendsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getFriendRequestReceivedCount(): Result<FriendRequestReceivedCountResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFriend(memberId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun postFriendRequest(friendRequest: FriendRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun postFriendWaterBalloon(friendWaterBalloonRequest: FriendWaterBalloonRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun patchFriendRequests(patchFriendRequest: PatchFriendRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getFriendsRequestsSent(
        lastId: Long?,
        size: Int,
    ): Result<FriendsRequestResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getFriendsRequestsReceived(
        lastId: Long?,
        size: Int,
    ): Result<FriendsRequestResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFriendsRequest(memberId: Long): Result<Unit> {
        TODO("Not yet implemented")
    }
}
