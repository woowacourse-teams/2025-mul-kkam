package com.mulkkam.ui.login

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.R
import com.mulkkam.databinding.ActivityLoginBinding
import com.mulkkam.ui.binding.BindingActivity

class LoginActivity : BindingActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.clKakaoLogin.setOnClickListener {
            loginWithKakao()
        }
    }

    private fun loginWithKakao() {
        val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("[Login Error]", "카카오 계정으로 로그인 실패", error)
            } else if (token != null) {
                showSnackBar(R.string.login_kakao_success)
                viewModel.loginWithKakao(token.accessToken)
            }
        }

        if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.Companion.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("[Login Error]", "카카오톡으로 로그인 실패", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.Companion.instance.loginWithKakaoAccount(
                        this,
                        callback = kakaoCallback,
                    )
                } else if (token != null) {
                    showSnackBar(R.string.login_kakao_success)
                    viewModel.loginWithKakao(token.accessToken)
                }
            }
        } else {
            UserApiClient.Companion.instance.loginWithKakaoAccount(
                this,
                callback = kakaoCallback,
            )
        }
    }

    private fun showSnackBar(message: Int) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}
