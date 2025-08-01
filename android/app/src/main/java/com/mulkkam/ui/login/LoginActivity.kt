package com.mulkkam.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.databinding.ActivityLoginBinding
import com.mulkkam.ui.binding.BindingActivity

class LoginActivity : BindingActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.clKakaoLogin.setOnClickListener {
            val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("kakao login", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Toast.makeText(this, "카카오 로그인 성공 !", Toast.LENGTH_SHORT).show()
                    Log.d("hwannow_log", token.accessToken)
                }
            }

            if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.Companion.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("kakao login", "카카오톡으로 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.Companion.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
                    } else if (token != null) {
                        Toast.makeText(this, "카카오 로그인 성공 !", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                UserApiClient.Companion.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
            }
        }
    }
}
