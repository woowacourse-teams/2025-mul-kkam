package com.mulkkam.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import com.mulkkam.ui.auth.splash.SplashRoute
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.login.LoginActivity
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.onboarding.terms.OnboardingTermsActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
                SplashRoute(
                    padding = PaddingValues(),
                    onNavigateToLogin = { startActivity(LoginActivity.newIntent(this)) },
                    onNavigateToMain = { startActivity(MainActivity.newIntent(this)) },
                    onNavigateToOnboarding = { startActivity(OnboardingTermsActivity.newIntent(this)) },
                )
            }
        }
    }
}
