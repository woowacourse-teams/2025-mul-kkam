package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.FriendsRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.FriendWaterBalloonRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import com.mulkkam.data.remote.model.response.friends.toDomain
import com.mulkkam.domain.model.friend.FriendsResult
import com.mulkkam.domain.model.friends.FriendRequestStatus
import com.mulkkam.domain.model.friends.FriendsRequestResult
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.FriendsRepository

class FriendsRepositoryImpl(
    private val friendsRemoteDataSource: FriendsRemoteDataSource,
) : FriendsRepository {
    override suspend fun getFriends(
        lastId: Long?,
        size: Int,
    ): MulKkamResult<FriendsResult> {
        val result = friendsRemoteDataSource.getFriends(lastId = lastId, size = size)
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getFriendRequestReceivedCount(): MulKkamResult<Long> {
        val result = friendsRemoteDataSource.getFriendRequestReceivedCount()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.count) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteFriend(memberId: Long): MulKkamResult<Unit> {
        val result = friendsRemoteDataSource.deleteFriend(memberId)
        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postFriendWaterBalloon(memberId: Long): MulKkamResult<Unit> {
        val result = friendsRemoteDataSource.postFriendWaterBalloon(FriendWaterBalloonRequest(memberId))
        return result.fold(
            onSuccess = { MulKkamResult(data = Unit) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getFriendsRequestReceived(
        lastId: Long?,
        size: Int,
    ): MulKkamResult<FriendsRequestResult> {
        val result = friendsRemoteDataSource.getFriendsRequestsReceived(lastId, size)
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getFriendsRequestSent(
        lastId: Long?,
        size: Int,
    ): MulKkamResult<FriendsRequestResult> {
        val result = friendsRemoteDataSource.getFriendsRequestsSent(lastId, size)
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteFriendsRequest(memberId: Long): MulKkamResult<Unit> {
        val result = friendsRemoteDataSource.deleteFriendsRequest(memberId)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postFriendRequest(id: Long): MulKkamResult<Unit> {
        val result = friendsRemoteDataSource.postFriendRequest(FriendRequest(id))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun patchFriendRequest(
        memberId: Long,
        status: FriendRequestStatus,
    ): MulKkamResult<Unit> {
        val result = friendsRemoteDataSource.patchFriendRequests(PatchFriendRequest(memberId, status.name))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
