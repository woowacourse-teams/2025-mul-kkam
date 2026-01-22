package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
import com.mulkkam.data.remote.model.request.friends.FriendRequest
import com.mulkkam.data.remote.model.request.friends.FriendWaterBalloonRequest
import com.mulkkam.data.remote.model.request.friends.PatchFriendRequest
import com.mulkkam.data.remote.model.response.friends.FriendRequestReceivedCountResponse
import com.mulkkam.data.remote.model.response.friends.FriendsRequestResponse
import com.mulkkam.data.remote.model.response.friends.FriendsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class FriendsRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : FriendsRemoteDataSource {
    override suspend fun getFriends(
        lastId: Long?,
        size: Int,
    ): Result<FriendsResponse> =
        safeApiCall {
            httpClient.get("/friends") {
                lastId?.let { parameter("lastId", it) }
                parameter("size", size)
            }
        }

    override suspend fun getFriendRequestReceivedCount(): Result<FriendRequestReceivedCountResponse> =
        safeApiCall {
            httpClient.get("/friend-requests/received-count")
        }

    override suspend fun deleteFriend(memberId: Long): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/friends") {
                parameter("memberId", memberId)
            }
        }

    override suspend fun postFriendRequest(friendRequest: FriendRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/friend-requests") {
                setBody(friendRequest)
            }
        }

    override suspend fun postFriendWaterBalloon(friendWaterBalloonRequest: FriendWaterBalloonRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/friends/reminder") {
                setBody(friendWaterBalloonRequest)
            }
        }

    override suspend fun patchFriendRequests(patchFriendRequest: PatchFriendRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/friend-requests") {
                setBody(patchFriendRequest)
            }
        }

    override suspend fun getFriendsRequestsSent(
        lastId: Long?,
        size: Int,
    ): Result<FriendsRequestResponse> =
        safeApiCall {
            httpClient.get("/friend-requests/sent") {
                lastId?.let { parameter("lastId", it) }
                parameter("size", size)
            }
        }

    override suspend fun getFriendsRequestsReceived(
        lastId: Long?,
        size: Int,
    ): Result<FriendsRequestResponse> =
        safeApiCall {
            httpClient.get("/friend-requests/received") {
                lastId?.let { parameter("lastId", it) }
                parameter("size", size)
            }
        }

    override suspend fun deleteFriendsRequest(memberId: Long): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/friend-requests") {
                parameter("memberId", memberId)
            }
        }
}
