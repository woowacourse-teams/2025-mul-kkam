package com.mulkkam.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.ui.auth.AuthNavGraph
import com.mulkkam.ui.auth.login.model.AuthPlatform
import com.mulkkam.ui.friends.FriendsNavGraph
import com.mulkkam.ui.history.HistoryNavGraph
import com.mulkkam.ui.home.HomeNavGraph
import com.mulkkam.ui.onboarding.OnboardingNavGraph
import com.mulkkam.ui.setting.SettingNavGraph

@Composable
fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = navigator.backStack,
        entryProvider = { route ->
            when (route) {
                is AuthRoute -> {
                    AuthNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        onLogin = onLogin,
                        snackbarHostState = snackbarHostState,
                    )
                }

                is OnboardingRoute -> {
                    OnboardingNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        snackbarHostState = snackbarHostState,
                    )
                }

                is HomeRoute -> {
                    HomeNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        snackbarHostState = snackbarHostState,
                    )
                }

                is HistoryRoute -> {
                    HistoryNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        snackbarHostState = snackbarHostState,
                    )
                }

                is FriendsRoute -> {
                    FriendsNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        snackbarHostState = snackbarHostState,
                    )
                }

                is SettingRoute -> {
                    SettingNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        snackbarHostState = snackbarHostState,
                    )
                }

                else -> {
                    entry(route) {}
                }
            }
        },
    )
}
