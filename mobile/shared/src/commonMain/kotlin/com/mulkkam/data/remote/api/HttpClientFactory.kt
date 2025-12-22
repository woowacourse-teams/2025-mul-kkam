package com.mulkkam.data.remote.api

import io.ktor.client.HttpClient

internal const val HEADER_AUTHORIZATION: String = "Authorization"
internal const val BEARER_PREFIX: String = "Bearer "

expect fun createHttpClient(
    baseUrl: String,
    getAccessToken: () -> String?,
    onUnauthorized: suspend () -> String?,
): HttpClient
