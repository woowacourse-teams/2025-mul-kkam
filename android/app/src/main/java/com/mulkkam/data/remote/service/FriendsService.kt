package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendsService {
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
