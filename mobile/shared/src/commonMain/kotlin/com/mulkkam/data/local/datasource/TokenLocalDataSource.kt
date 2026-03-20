package com.mulkkam.data.local.datasource

interface TokenLocalDataSource {
    var accessToken: String?

    var refreshToken: String?

    var fcmToken: String?

    fun saveAccessToken(token: String)

    fun deleteAccessToken()

    fun saveRefreshToken(token: String)

    fun deleteRefreshToken()

    fun saveFcmToken(token: String)

    fun deleteFcmToken()
}
