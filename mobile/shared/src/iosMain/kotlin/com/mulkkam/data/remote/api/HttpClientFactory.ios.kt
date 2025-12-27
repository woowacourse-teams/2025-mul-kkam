package com.mulkkam.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createHttpClient(
    baseUrl: String,
    getAccessToken: () -> String?,
    onUnauthorized: suspend () -> String?,
): HttpClient =
    HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                },
            )
        }

        install(Logging) {
            level = LogLevel.BODY
        }

        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
            getAccessToken()?.let { token ->
                header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token")
            }
        }
    }
