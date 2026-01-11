package com.mulkkam.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.MulKkamApp
import com.mulkkam.ui.auth.login.model.AuthPlatform

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamApp(onLogin = ::login)
        }
    }

    fun login(
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        when (authPlatform) {
            AuthPlatform.KAKAO -> {
                loginWithKakao(onSuccess, onError)
            }

            else -> {
                Unit
            }
        }
    }

    private fun loginWithKakao(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            loginWithKakaoTalk(onSuccess, onError)
        } else {
            loginWithKakaoAccount(onSuccess, onError)
        }
    }

    private fun loginWithKakaoTalk(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            when {
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    Unit
                }

                error != null -> {
                    loginWithKakaoAccount(onSuccess, onError)
                }

                else -> {
                    token?.let {
                        onSuccess(it.accessToken)
                    }
                }
            }
        }
    }

    private fun loginWithKakaoAccount(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                onError(error.message ?: "Login Error")
            } else {
                token?.let {
                    onSuccess(it.accessToken)
                }
            }
        }
    }
}
