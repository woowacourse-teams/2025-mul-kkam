package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.response.friends.FriendRequestReceivedCountResponse
import com.mulkkam.data.remote.model.response.friends.FriendsResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
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
}
