package com.mulkkam.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.domain.model.UserAuthState
import com.mulkkam.domain.model.UserAuthState.ACTIVE_USER
import com.mulkkam.domain.model.UserAuthState.UNONBOARDED
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.onboarding.terms.OnboardingTermsActivity
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModel()
    private var isSplashFinished: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
                SplashScreen {
                    isSplashFinished = true
                    navigateToNextScreen(viewModel.authUiState.value)
                }
            }
        }
        initObservers()
    }

    private fun initObservers() {
        viewModel.authUiState.collectWithLifecycle(this) { authUiState ->
            navigateToNextScreen(authUiState)
        }
    }

    private fun navigateToNextScreen(authUiState: MulKkamUiState<UserAuthState>) {
        if (isSplashFinished.not()) return

        val intent =
            when (authUiState) {
                is MulKkamUiState.Success<UserAuthState> -> {
                    when (authUiState.data) {
                        UNONBOARDED -> OnboardingTermsActivity.newIntent(this)
                        ACTIVE_USER -> MainActivity.newIntent(this)
                    }
                }

                is MulKkamUiState.Loading -> return
                is MulKkamUiState.Idle -> return
                is MulKkamUiState.Failure -> LoginActivity.newIntent(this)
            }
        startActivity(intent)
        finish()
    }
}
