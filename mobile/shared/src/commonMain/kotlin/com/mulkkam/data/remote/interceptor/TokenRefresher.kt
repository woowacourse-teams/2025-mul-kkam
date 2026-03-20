package com.mulkkam.data.remote.interceptor

import com.mulkkam.data.local.datasource.DevicesLocalDataSource
import com.mulkkam.data.local.datasource.TokenLocalDataSource
import com.mulkkam.data.remote.datasource.AuthRemoteDataSource
import com.mulkkam.domain.model.result.toMulKkamResult
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TokenRefresher(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val deviceLocalDataSource: DevicesLocalDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
) {
    private val mutex: Mutex = Mutex()
    private var currentJob: Deferred<String?>? = null

    fun refresh(): String? =
        runBlocking {
            mutex.withLock {
                currentJob?.let { return@runBlocking it.await() }

                val newJob =
                    async(Dispatchers.IO, start = CoroutineStart.LAZY) {
                        val refreshToken: String = tokenLocalDataSource.refreshToken ?: return@async null
                        val deviceUuid: String = deviceLocalDataSource.deviceUuid ?: return@async null

                        val result =
                            runCatching {
                                authRemoteDataSource.postAuthTokenReissue(
                                    refreshToken,
                                    deviceUuid,
                                )
                            }.getOrNull() ?: return@async null

                        return@async runCatching {
                            val info = result.toMulKkamResult().getOrError()
                            tokenLocalDataSource.saveAccessToken(info.accessToken)
                            tokenLocalDataSource.saveRefreshToken(info.refreshToken)
                            info.accessToken
                        }.getOrElse {
                            null
                        }
                    }

                currentJob = newJob

                try {
                    newJob.start()
                    newJob.await()
                } finally {
                    currentJob = null
                }
            }
        }
}
