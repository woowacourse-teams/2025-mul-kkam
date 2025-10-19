package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.service.FriendsService
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.FriendsRepository

class FriendsRepositoryImpl(
    private val friendsService: FriendsService,
) : FriendsRepository {
    override suspend fun getFriends(): MulKkamResult<Unit> {
        val result = friendsService.getFriends()
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
