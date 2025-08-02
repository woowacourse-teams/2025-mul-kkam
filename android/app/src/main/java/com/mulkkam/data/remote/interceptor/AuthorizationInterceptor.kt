package com.mulkkam.data.remote.interceptor

import com.mulkkam.di.PreferenceInjection
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.text.format

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val authorizationValue = HEADER_VALUE_AUTHORIZATION.format(PreferenceInjection.tokenPreference.accessToken)

        builder.addHeader(HEADER_NAME_AUTHORIZATION, authorizationValue)
        return chain.proceed(builder.build())
    }

    companion object {
        private const val HEADER_NAME_AUTHORIZATION = "Authorization"
        private const val HEADER_VALUE_AUTHORIZATION = "Bearer %s"
    }
}
