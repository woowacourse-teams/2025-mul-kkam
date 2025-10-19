package com.mulkkam.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.R
import com.mulkkam.databinding.ActivityLoginBinding
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.UserAuthState
import com.mulkkam.ui.model.UserAuthState.ACTIVE_USER
import com.mulkkam.ui.model.UserAuthState.UNONBOARDED
import com.mulkkam.ui.onboarding.OnboardingActivity
import com.mulkkam.ui.splash.dialog.AppUpdateDialogFragment
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.getAppVersion
import com.mulkkam.ui.util.extensions.setSingleClickListener

class LoginActivity : BindingActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkAppVersion(getAppVersion())
        initClickListeners()
        initObservers()
        initDoubleBackToExit()
    }

    private fun initClickListeners() {
        binding.clKakaoLogin.setSingleClickListener {
            loginWithKakao()
        }
    }

    private fun loginWithKakao() {
        mulKkamLogger.info(LogEvent.USER_AUTH, "Kakao Login Attempted")
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            loginWithKakaoTalk()
        } else {
            loginWithKakaoAccount()
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            when {
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> Unit

                error != null -> {
                    mulKkamLogger.error(LogEvent.USER_AUTH, "Kakao Login Failed: ${error.message}")
                    loginWithKakaoAccount()
                }

                else -> handleKakaoLoginResult(token)
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                mulKkamLogger.error(LogEvent.USER_AUTH, "Kakao Login Failed: ${error.message}")
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
        viewModel.authUiState.observe(this) { authUiState ->
            handleAuthUiState(authUiState)
        }

        viewModel.isAppOutdated.observe(this) { isAppOutdated ->
            if (isAppOutdated) showUpdateDialog()
        }
    }

    private fun showUpdateDialog() {
        if (supportFragmentManager.findFragmentByTag(AppUpdateDialogFragment.TAG) != null) return
        AppUpdateDialogFragment
            .newInstance()
            .show(supportFragmentManager, AppUpdateDialogFragment.TAG)
    }

    private fun handleAuthUiState(authUiState: MulKkamUiState<UserAuthState>) {
        when (authUiState) {
            is MulKkamUiState.Success<UserAuthState> -> {
                navigateToNextScreen(authUiState)
            }

            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Failure -> {
                CustomSnackBar.make(binding.root, getString(R.string.network_check_error), R.drawable.ic_alert_circle).show()
            }
        }
    }

    private fun navigateToNextScreen(authUiState: MulKkamUiState.Success<UserAuthState>) {
        val intent =
            when (authUiState.data) {
                UNONBOARDED -> OnboardingActivity.newIntent(this)
                ACTIVE_USER -> MainActivity.newIntent(this)
            }
        startActivity(intent)
        finish()
    }

    private fun initDoubleBackToExit() {
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
