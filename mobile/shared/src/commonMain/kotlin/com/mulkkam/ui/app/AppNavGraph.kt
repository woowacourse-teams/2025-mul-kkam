package com.mulkkam.ui.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.app.login.LoginScreen
import com.mulkkam.ui.app.splash.SplashScreen
import com.mulkkam.ui.navigation.AppRoute
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.entry

object AppNavGraph {
    @Composable
    fun entryProvider(
        route: AppRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
    ): NavEntry<AppRoute> =
        when (route) {
            is AppRoute.Splash -> {
                entry(route) {
                    SplashScreen(
                        padding = padding,
                        onNavigateToLogin = navigator::navigateToLogin,
                        onNavigateToMain = navigator::navigateToHome,
                    )
                }
            }

            is AppRoute.Login -> {
                entry(route) {
                    LoginScreen(
                        padding = padding,
                        onNavigateToOnboarding = navigator::navigateToOnboardingTerms,
                    )
                }
            }
        }
}
