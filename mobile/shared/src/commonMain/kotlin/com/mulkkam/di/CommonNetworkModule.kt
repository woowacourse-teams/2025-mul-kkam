package com.mulkkam.di

import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.data.remote.interceptor.NetworkLogger
import com.mulkkam.data.remote.interceptor.TokenRefresher
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

fun commonNetworkModule(baseUrl: String): Module =
    module {
        single<HttpClient> {
            createHttpClient(
                baseUrl = baseUrl,
                httpClientEngine = get(),
                tokenLocalDataSource = get(),
                tokenRefresher = lazy { get() },
                logger = get(),
            )
        }

        single {
            TokenRefresher(get(), get(), get())
        }
    }

fun createHttpClient(
    baseUrl: String,
    httpClientEngine: HttpClientEngine,
    tokenLocalDataSource: TokenLocalDataSource,
    tokenRefresher: Lazy<TokenRefresher>,
    logger: Logger,
): HttpClient =
    HttpClient(httpClientEngine) {
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
            this.logger = NetworkLogger(logger)
            level = LogLevel.ALL
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, request ->
                logger.error(
                    event = LogEvent.NETWORK,
                    message =
                        buildString {
                            appendLine("\n☠️ Network request failed:")
                            appendLine("URL: ${request.url}")
                            append("Error: ${exception.message}")
                        },
                )
                throw exception
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    tokenLocalDataSource.accessToken?.let { BearerTokens(it, "") }
                }

                refreshTokens {
                    tokenRefresher.value.refresh()?.let { BearerTokens(it, "") }
                }
            }
        }

        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }
    }
