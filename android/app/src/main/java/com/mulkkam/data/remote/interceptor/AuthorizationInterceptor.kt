package com.mulkkam.data.remote.interceptor

import com.mulkkam.data.remote.model.error.ResponseError
import com.mulkkam.di.PreferenceInjection.tokenPreference
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.net.HttpURLConnection

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. 최초 요청
        val initialRequest =
            chain
                .request()
                .newBuilder()
                .addHeader(
                    HEADER_NAME_AUTHORIZATION,
                    HEADER_VALUE_AUTHORIZATION.format(tokenPreference.accessToken),
                ).build()

        val response = chain.proceed(initialRequest)

        // 2. 401 처리
        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED ||
            !hasAccessTokenExpiredError(
                response,
            )
        ) {
            return response
        }

        // 기존 response 닫기
        response.close()

        // 3. 토큰 재발급
        val newAccessToken = TokenRefresher.refresh()

        if (newAccessToken == null) {
            return response
        }

        // 4. 새로운 토큰으로 재시도
        val newRequest =
            chain
                .request()
                .newBuilder()
                .header(HEADER_NAME_AUTHORIZATION, HEADER_VALUE_AUTHORIZATION.format(newAccessToken))
                .build()

        return chain.proceed(newRequest)
    }

    private fun hasAccessTokenExpiredError(response: Response): Boolean =
        runCatching {
            val rawBody = response.body.string()
            val json = JSONObject(rawBody)
            json.optString(ERROR_BODY) == ResponseError.AccountError.Unauthorized.code
        }.getOrElse { false }
            .also { response.close() }

    companion object {
        private const val HEADER_NAME_AUTHORIZATION: String = "Authorization"
        private const val HEADER_VALUE_AUTHORIZATION: String = "Bearer %s"

        private const val ERROR_BODY: String = "error"
    }
}
