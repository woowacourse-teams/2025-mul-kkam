package com.mulkkam.ui.auth

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.auth.login.LoginRoute
import com.mulkkam.ui.auth.splash.SplashRoute
import com.mulkkam.ui.navigation.AuthRoute
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.entry

object AuthNavGraph {
    @Composable
    fun entryProvider(
        route: AuthRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
    ): NavEntry<AuthRoute> =
        when (route) {
            is AuthRoute.Splash -> {
                entry(route) {
                    SplashRoute(
                        padding = padding,
                        onNavigateToLogin = navigator::navigateToLogin,
                        onNavigateToMain = navigator::navigateToMain,
                        onNavigateToOnboarding = navigator::navigateToOnboardingTerms,
                    )
                }
            }

            is AuthRoute.Login -> {
                entry(route) {
                    LoginRoute(
                        padding = padding,
                        onNavigateToOnboarding = navigator::navigateToOnboardingTerms,
                    )
                }
            }
        }
}
