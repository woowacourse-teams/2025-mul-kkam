package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import com.mulkkam.data.remote.model.response.friends.FriendRequestReceivedCountResponse
import com.mulkkam.data.remote.model.response.friends.FriendsRequestResponse
import com.mulkkam.data.remote.model.response.friends.FriendsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendsService {
    @GET("/friends")
    suspend fun getFriends(
        @Query("lastId") lastId: Long? = null,
        @Query("size") size: Int,
    ): Result<FriendsResponse>

    @GET("/friend-requests/received-count")
    suspend fun getFriendRequestReceivedCount(): Result<FriendRequestReceivedCountResponse>

    @DELETE("/friends")
    suspend fun deleteFriend(
        @Query("memberId") memberId: Long,
    ): Result<Unit>

    @POST("/friend-requests")
    suspend fun postFriendRequest(
        @Body friendRequest: FriendRequest,
    ): Result<Unit>

    @PATCH("/friend-requests")
    suspend fun patchFriendRequests(
        @Body patchFriendRequest: PatchFriendRequest,
    ): Result<Unit>

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

    @DELETE("/friend-requests")
    suspend fun deleteFriendsRequest(
        @Query("memberId") memberId: Long,
    ): Result<Unit>
}
