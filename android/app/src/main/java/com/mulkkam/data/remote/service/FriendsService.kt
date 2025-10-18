package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.friends.FriendRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface FriendsService {
    @POST("/friend-request")
    suspend fun postFriendRequest(
        @Body friendRequest: FriendRequest,
    ): Result<Unit>
}
