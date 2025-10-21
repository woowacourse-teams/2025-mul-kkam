package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import com.mulkkam.data.remote.model.response.friends.toDomain
import com.mulkkam.data.remote.service.FriendsService
import com.mulkkam.domain.model.friends.FriendsRequestResult
import com.mulkkam.domain.model.members.Status
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.FriendsRepository
import javax.inject.Inject

class FriendsRepositoryImpl
    @Inject
    constructor(
        private val friendsService: FriendsService,
    ) : FriendsRepository {
        override suspend fun getFriends(): MulKkamResult<Unit> {
            val result = friendsService.getFriends()
            return result.fold(
                onSuccess = { MulKkamResult() },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun getFriendsRequestReceived(
            lastId: Long?,
            size: Int,
        ): MulKkamResult<FriendsRequestResult> {
            val result = friendsService.getFriendsRequestsReceived(lastId, size)
            return result.fold(
                onSuccess = { MulKkamResult(data = it.toDomain()) },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun getFriendsRequestSent(
            lastId: Long?,
            size: Int,
        ): MulKkamResult<FriendsRequestResult> {
            val result = friendsService.getFriendsRequestsSent(lastId, size)
            return result.fold(
                onSuccess = { MulKkamResult(data = it.toDomain()) },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun deleteFriendsRequest(memberId: Long): MulKkamResult<Unit> {
            val result = friendsService.deleteFriendsRequest(memberId)
            return result.fold(
                onSuccess = { MulKkamResult() },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun postFriendRequest(id: Long): MulKkamResult<Unit> {
            val result = friendsService.postFriendRequest(FriendRequest(id))
            return result.fold(
                onSuccess = { MulKkamResult() },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }

        override suspend fun patchFriendRequest(
            memberId: Long,
            status: Status,
        ): MulKkamResult<Unit> {
            val result = friendsService.patchFriendRequests(PatchFriendRequest(memberId, status.name))
            return result.fold(
                onSuccess = { MulKkamResult() },
                onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
            )
        }
    }
