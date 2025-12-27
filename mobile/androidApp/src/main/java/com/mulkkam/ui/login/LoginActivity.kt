package com.mulkkam.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.mulkkam.R
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.UserAuthState.ACTIVE_USER
import com.mulkkam.domain.model.UserAuthState.UNONBOARDED
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.onboarding.terms.OnboardingTermsActivity
import com.mulkkam.ui.util.extensions.getAppVersion
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModel()
    private val logger: Logger by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkAppVersion(getAppVersion())
        setContent {
            MulKkamTheme {
                LoginRoute(
                    viewModel = viewModel,
                    onLoginWithKakao = ::loginWithKakao,
                    onNavigateToNextScreen = ::navigateToNextScreen,
                    onNavigateToPlayStoreAndExit = ::openPlayStoreAndExit,
                )
            }
        }
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

    private fun openPlayStoreAndExit() {
        val appPackageName: String = packageName

        val playStoreUri: Uri = getString(R.string.play_store_app, appPackageName).toUri()
        val webUri: Uri = getString(R.string.play_store_web, appPackageName).toUri()

        val playStoreIntent =
            Intent(Intent.ACTION_VIEW, playStoreUri).apply {
                setPackage(getString(R.string.play_store))
                addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
                )
            }
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)

        runCatching { startActivity(playStoreIntent) }
            .recoverCatching { startActivity(webIntent) }

        finishAffinity()
        finishAndRemoveTask()
    }

    private fun navigateToNextScreen(userAuthState: UserAuthState) {
        val intent =
            when (userAuthState) {
                UNONBOARDED -> OnboardingTermsActivity.newIntent(this)
                ACTIVE_USER -> MainActivity.newIntent(this)
            }
        startActivity(intent)
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}
