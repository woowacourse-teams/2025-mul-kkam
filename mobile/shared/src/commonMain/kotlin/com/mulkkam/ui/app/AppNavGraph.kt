package com.mulkkam.ui.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.AppRoute
import com.mulkkam.ui.navigation.MainNavigator

object AppNavGraph {
    @Composable
    fun entryProvider(
        route: com.mulkkam.ui.navigation.AppRoute,
        padding: PaddingValues,
        navigator: com.mulkkam.ui.navigation.MainNavigator,
    ): com.mulkkam.ui.core.NavEntry<com.mulkkam.ui.navigation.AppRoute> =
        when (route) {
            is com.mulkkam.ui.navigation.AppRoute.Splash -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.app.splash.SplashScreen(
                        padding = padding,
                        onNavigateToLogin = com.mulkkam.ui.navigation.MainNavigator::navigateToLogin,
                        onNavigateToMain = com.mulkkam.ui.navigation.MainNavigator::navigateToHome,
                    )
                }
            }

            is com.mulkkam.ui.navigation.AppRoute.Login -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.app.login.LoginScreen(
                        padding = padding,
                        onNavigateToOnboarding = com.mulkkam.ui.navigation.MainNavigator::navigateToOnboardingTerms,
                    )
                }
            }
        }
}
