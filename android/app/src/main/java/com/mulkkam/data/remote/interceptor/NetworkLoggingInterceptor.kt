package com.mulkkam.data.remote.interceptor

import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class NetworkLoggingInterceptor(
    private val logger: Logger,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestTime = System.nanoTime()

        val requestBodyString =
            request.body?.let { body ->
                val buffer = Buffer()
                body.writeTo(buffer)
                buffer.readString(Charset.forName(UTF_8))
            } ?: NO_BODY

        logger.debug(
            event = LogEvent.NETWORK,
            message =
                buildString {
                    appendLine("\n🏹️ Request: ${request.method} ${request.url}")
                    append("Body: $requestBodyString")
                },
        )

        return runCatching {
            val response = chain.proceed(request)
            val tookDuration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestTime)

            val source = response.body.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            val responseBodyString = buffer.clone().readString(Charset.forName(UTF_8))

            logger.debug(
                event = LogEvent.NETWORK,
                message =
                    buildString {
                        appendLine("\n🛡️️ Response: ${response.code} ${response.message} (${tookDuration}ms)")
                        appendLine("URL: ${response.request.url}")
                        append("Body: $responseBodyString")
                    },
            )

            response
        }.onFailure { e ->
            logger.error(
                event = LogEvent.NETWORK,
                message = "\n☠️ Network request failed: ${e.message}",
            )
        }.getOrThrow()
    }

    companion object {
        private const val UTF_8 = "UTF-8"
        private const val NO_BODY = "No Body"
    }
}
