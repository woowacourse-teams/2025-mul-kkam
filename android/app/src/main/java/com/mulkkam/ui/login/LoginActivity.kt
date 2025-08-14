package com.mulkkam.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.R
import com.mulkkam.databinding.ActivityLoginBinding
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.AppAuthState.ACTIVE_USER
import com.mulkkam.ui.model.AppAuthState.UNONBOARDED
import com.mulkkam.ui.onboarding.OnboardingActivity
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class LoginActivity : BindingActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListeners()
        initObservers()
        setupDoubleBackToExit()
    }

    private fun initClickListeners() {
        binding.clKakaoLogin.setSingleClickListener {
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
            viewModel.loginWithKakao(it.accessToken)
        }
    }

    private fun initObservers() {
        viewModel.authState.observe(this) { authState ->
            val intent =
                when (authState) {
                    UNONBOARDED -> OnboardingActivity.newIntent(this@LoginActivity)
                    ACTIVE_USER -> MainActivity.newIntent(this@LoginActivity)
                    else -> throw MulKkamError.NotFoundError.Member
                }

            startActivity(intent)
        }
    }

    private fun setupDoubleBackToExit() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - backPressedTime >= BACK_PRESS_THRESHOLD) {
                        backPressedTime = System.currentTimeMillis()
                        CustomSnackBar
                            .make(
                                binding.root,
                                getString(R.string.main_main_back_press_exit_message),
                                R.drawable.ic_info_circle,
                            ).show()
                    } else {
                        finishAffinity()
                    }
                }
            },
        )
    }

    companion object {
        private const val BACK_PRESS_THRESHOLD: Long = 2000L

        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}
