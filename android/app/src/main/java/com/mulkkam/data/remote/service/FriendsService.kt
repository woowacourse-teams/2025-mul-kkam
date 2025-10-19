package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.friends.PatchFriendsRequest
import com.mulkkam.data.remote.model.response.friends.FriendsRequestReceivedResponse
import com.mulkkam.data.remote.model.response.friends.FriendsRequestSentResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendsService {
    @GET("/friends")
    suspend fun getFriends(): Result<Unit>

    @GET("/friends/requests/sent")
    suspend fun getFriendsRequestsSent(
        @Query("lastId") lastId: Long?,
        @Query("size") size: Int,
    ): Result<FriendsRequestSentResponse>

    @GET("/friends/requests/received")
    suspend fun getFriendsRequestsReceived(
        @Query("lastId") lastId: Long?,
        @Query("size") size: Int,
    ): Result<FriendsRequestReceivedResponse>

    @PATCH("/friend-requests/{requestId}")
    suspend fun patchFriendsRequests(
        @Path("requestId") requestId: Long,
        @Body patchFriendsRequest: PatchFriendsRequest,
    ): Result<Unit>

    @DELETE("/friend-requests/{requestId}")
    suspend fun deleteFriendsRequest(
        @Path("requestId") requestId: Long,
    ): Result<Unit>
}
