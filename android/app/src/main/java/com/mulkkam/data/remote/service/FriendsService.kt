package com.mulkkam.data.remote.service

import retrofit2.http.GET
import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendsService {
    @GET("/friends")
    suspend fun getFriends(): Result<Unit>

    @POST("/friend-requests")
    suspend fun postFriendRequest(
        @Body friendRequest: FriendRequest,
    ): Result<Unit>

    @PATCH("/friend-requests/{requestId}")
    suspend fun patchFriendRequests(
        @Path("requestId") requestId: Long,
        @Body patchFriendRequest: PatchFriendRequest,
    ): Result<Unit>
}
