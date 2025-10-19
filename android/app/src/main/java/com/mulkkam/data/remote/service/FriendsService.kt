package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.friends.PatchFriendsRequest
import com.mulkkam.data.remote.model.response.friends.FriendsRequestResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface FriendsService {
    @GET("/friends")
    suspend fun getFriends(): Result<Unit>

    @GET("/friend-requests/sent")
    suspend fun getFriendsRequestsSent(
        @Query("lastId") lastId: Long?,
        @Query("size") size: Int,
    ): Result<FriendsRequestResponse>

    @GET("/friend-requests/received")
    suspend fun getFriendsRequestsReceived(
        @Query("lastId") lastId: Long?,
        @Query("size") size: Int,
    ): Result<FriendsRequestResponse>

    @PATCH("/friend-requests")
    suspend fun patchFriendsRequests(
        @Body patchFriendsRequest: PatchFriendsRequest,
    ): Result<Unit>

    @DELETE("/friend-requests")
    suspend fun deleteFriendsRequest(
        @Query("memberId") memberId: Long,
    ): Result<Unit>
}
