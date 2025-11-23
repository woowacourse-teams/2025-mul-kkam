package com.mulkkam.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.UserAuthState
import com.mulkkam.ui.model.UserAuthState.ACTIVE_USER
import com.mulkkam.ui.model.UserAuthState.UNONBOARDED
import com.mulkkam.ui.onboarding.OnboardingActivity
import com.mulkkam.ui.splash.dialog.AppUpdateDialogFragment
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import com.mulkkam.ui.util.extensions.getAppVersion
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkAppVersion(getAppVersion())
        setContent {
            MulkkamTheme {
                LoginRoute(
                    viewModel = viewModel,
                    onLoginWithKakao = ::loginWithKakao,
                    onNavigateToNextScreen = ::navigateToNextScreen,
                )
            }
        }
        collectIsAppOutdated()
    }

    private fun loginWithKakao() {
        logger.info(LogEvent.USER_AUTH, "Kakao Login Attempted")
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
                    logger.error(LogEvent.USER_AUTH, "Kakao Login Failed: ${error.message}")
                    loginWithKakaoAccount()
                }

                else -> handleKakaoLoginResult(token)
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                logger.error(LogEvent.USER_AUTH, "Kakao Login Failed: ${error.message}")
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

    private fun collectIsAppOutdated() {
        viewModel.isAppOutdated.collectWithLifecycle(this, Lifecycle.State.STARTED) { isAppOutdated ->
            if (isAppOutdated) showUpdateDialog()
        }
    }

    private fun showUpdateDialog() {
        if (supportFragmentManager.findFragmentByTag(AppUpdateDialogFragment.TAG) != null) return
        AppUpdateDialogFragment
            .newInstance()
            .show(supportFragmentManager, AppUpdateDialogFragment.TAG)
    }

    private fun navigateToNextScreen(userAuthState: UserAuthState) {
        val intent =
            when (userAuthState) {
                UNONBOARDED -> OnboardingActivity.newIntent(this)
                ACTIVE_USER -> MainActivity.newIntent(this)
            }
        startActivity(intent)
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}
