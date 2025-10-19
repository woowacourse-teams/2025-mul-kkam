package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface FriendsService {
    @GET("/friends")
    suspend fun getFriends(): Result<Unit>

    @POST("/friend-requests")
    suspend fun postFriendRequest(
        @Body friendRequest: FriendRequest,
    ): Result<Unit>

    @PATCH("/friend-requests")
    suspend fun patchFriendRequests(
        @Body patchFriendRequest: PatchFriendRequest,
    ): Result<Unit>
}
