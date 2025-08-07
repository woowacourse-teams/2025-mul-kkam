package com.mulkkam.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.R
import com.mulkkam.databinding.ActivityLoginBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.main.MainActivity

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
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            loginWithKakaoTalk()
        } else {
            loginWithKakaoAccount()
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if (error != null) {
                Log.e("[Login Error]", "카카오톡으로 로그인 실패", error)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }
                loginWithKakaoAccount()
            } else {
                handleKakaoLoginResult(token)
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                Log.e("[Login Error]", "카카오 계정으로 로그인 실패", error)
            } else {
                handleKakaoLoginResult(token)
            }
        }
    }

    private fun handleKakaoLoginResult(token: OAuthToken?) {
        token?.let {
            showToast(R.string.login_kakao_success)
            viewModel.loginWithKakao(it.accessToken)
            navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {
        val intent = MainActivity.newIntent(this)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}
