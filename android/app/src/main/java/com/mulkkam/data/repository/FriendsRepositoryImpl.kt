package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.service.FriendsService
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.FriendsRepository

class FriendsRepositoryImpl(
    private val friendsService: FriendsService,
) : FriendsRepository {
    override suspend fun postFriendRequest(id: Long): MulKkamResult<Unit> {
        val result = friendsService.postFriendRequest(FriendRequest(id))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
