package com.mulkkam.data.remote.interceptor

import com.mulkkam.data.remote.model.request.auth.AuthReissueRequest
import com.mulkkam.di.PreferenceInjection
import com.mulkkam.di.ServiceInjection.authService
import com.mulkkam.domain.model.result.toMulKkamResult
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.net.HttpURLConnection
import kotlin.text.format

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. 최초 요청
        val initialRequest =
            chain
                .request()
                .newBuilder()
                .addHeader(
                    HEADER_NAME_AUTHORIZATION,
                    HEADER_VALUE_AUTHORIZATION.format(PreferenceInjection.tokenPreference.accessToken),
                ).build()

        val response = chain.proceed(initialRequest)

        // 2. 401 처리
        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED || !hasAccessTokenExpiredError(response)) return response

        val refreshToken = PreferenceInjection.tokenPreference.refreshToken ?: return response

        // 기존 response 닫기
        response.close()

        // 3. 토큰 재발급
        val refreshResponse =
            runBlocking {
                try {
                    authService.postAuthTokenReissue(AuthReissueRequest(refreshToken))
                } catch (e: Exception) {
                    null
                }
            } ?: return response

        if (refreshResponse.isSuccess != true) return response

        // 4. 새 토큰 저장
        val tokens = refreshResponse.toMulKkamResult().getOrError()
        PreferenceInjection.tokenPreference.saveAccessToken(tokens.accessToken)
        PreferenceInjection.tokenPreference.saveRefreshToken(tokens.refreshToken)

        // 5. 새 요청 재시도
        val newRequest =
            chain
                .request()
                .newBuilder()
                .addHeader(
                    HEADER_NAME_AUTHORIZATION,
                    HEADER_VALUE_AUTHORIZATION.format(PreferenceInjection.tokenPreference.accessToken),
                ).build()

        return chain.proceed(newRequest)
    }

    private fun hasAccessTokenExpiredError(response: Response): Boolean {
        val rawBody = response.body.string()

        return try {
            val json = JSONObject(rawBody)
            json.optString(ERROR_BODY) == ERROR_BODY_TOKEN_EXPIRED
        } catch (e: Exception) {
            false
        } finally {
            response.close()
        }
    }

    companion object {
        private const val HEADER_NAME_AUTHORIZATION: String = "Authorization"
        private const val HEADER_VALUE_AUTHORIZATION: String = "Bearer %s"

        private const val ERROR_BODY: String = "error"
        private const val ERROR_BODY_TOKEN_EXPIRED: String = "Unauthorized"
    }
}
