package com.mulkkam.ui.auth

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.auth.login.LoginRoute
import com.mulkkam.ui.auth.login.model.AuthPlatform
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
        onLogin: (
            authPlatform: AuthPlatform,
            onSuccess: (token: String) -> Unit,
            onError: (errorMessage: String) -> Unit,
        ) -> Unit,
        appVersion: String,
        snackbarHostState: SnackbarHostState,
    ): NavEntry<AuthRoute> =
        when (route) {
            is AuthRoute.Splash -> {
                entry(route) {
                    SplashRoute(
                        padding = padding,
                        onNavigateToLogin = navigator::navigateToLogin,
                        onNavigateToMain = navigator::navigateToHome,
                        onNavigateToOnboarding = navigator::navigateToOnboardingTerms,
                    )
                }
            }

            is AuthRoute.Login -> {
                entry(route) {
                    LoginRoute(
                        padding = padding,
                        onNavigateToOnboarding = navigator::navigateToOnboardingTerms,
                        onNavigateToMain = navigator::navigateToHome,
                        onLogin = onLogin,
                        snackbarHostState = snackbarHostState,
                        appVersion = appVersion,
                    )
                }
            }
        }
}
