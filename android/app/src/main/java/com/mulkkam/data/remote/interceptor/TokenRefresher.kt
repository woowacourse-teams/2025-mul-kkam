package com.mulkkam.data.remote.interceptor

import com.mulkkam.data.local.preference.DevicesPreference
import com.mulkkam.data.local.preference.TokenPreference
import com.mulkkam.data.remote.model.request.auth.AuthReissueRequest
import com.mulkkam.data.remote.service.AuthService
import com.mulkkam.domain.model.result.toMulKkamResult
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefresher
    @Inject
    constructor(
        private val tokenPreference: TokenPreference,
        private val devicesPreference: DevicesPreference,
        private val authService: dagger.Lazy<AuthService>,
    ) {
        private val currentJob = AtomicReference<Deferred<String?>?>(null)

        fun refresh(): String? =
            runBlocking {
                currentJob.get()?.let { return@runBlocking it.await() }

                val newJob =
                    async(Dispatchers.IO, start = CoroutineStart.LAZY) {
                        val refreshToken = tokenPreference.refreshToken ?: return@async null
                        val deviceUuid = devicesPreference.deviceUuid ?: return@async null

                        val result =
                            runCatching {
                                authService.get().postAuthTokenReissue(
                                    AuthReissueRequest(
                                        refreshToken,
                                        deviceUuid,
                                    ),
                                )
                            }.getOrNull() ?: return@async null

                        return@async runCatching {
                            val info = result.toMulKkamResult().getOrError()
                            tokenPreference.saveAccessToken(info.accessToken)
                            tokenPreference.saveRefreshToken(info.refreshToken)
                            info.accessToken
                        }.getOrElse {
                            null
                        }
                    }

                if (currentJob.compareAndSet(null, newJob)) {
                    try {
                        newJob.start()
                        newJob.await()
                    } finally {
                        currentJob.set(null)
                    }
                } else {
                    newJob.cancel()
                    currentJob.get()?.await()
                }
            }
    }
