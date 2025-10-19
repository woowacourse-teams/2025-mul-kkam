package com.mulkkam.data.remote.service

import retrofit2.http.GET

interface FriendsService {
    @GET("/friends")
    suspend fun getFriends(): Result<Unit>
}
