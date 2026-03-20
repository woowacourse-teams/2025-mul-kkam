package com.mulkkam.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.mulkkam.ui.auth.AuthNavGraph
import com.mulkkam.ui.auth.login.model.AuthPlatform
import com.mulkkam.ui.friends.FriendsNavGraph
import com.mulkkam.ui.history.HistoryNavGraph
import com.mulkkam.ui.home.HomeNavGraph
import com.mulkkam.ui.onboarding.OnboardingNavGraph
import com.mulkkam.ui.setting.SettingNavGraph
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

const val ONBOARDING_SCOPE: String = "ONBOARDING_SCOPE"

@Composable
fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    onRegisterPushNotification: (
        onTokenUpdated: (token: String) -> Unit,
        onPermissionUpdated: (isGranted: Boolean) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    onRequestInitialPermissions: () -> Unit,
    appVersion: String,
    snackbarHostState: SnackbarHostState,
) {
    val koin = getKoin()

    val isOnboardingActive = navigator.backStack.any { it is OnboardingRoute }

    val onboardingScope =
        remember(isOnboardingActive) {
            if (isOnboardingActive) {
                koin.getOrCreateScope(
                    scopeId = "OnboardingScopeId",
                    qualifier = named(ONBOARDING_SCOPE),
                )
            } else {
                null
            }
        }

    DisposableEffect(onboardingScope) {
        onDispose {
            onboardingScope?.close()
        }
    }

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
                        appVersion = appVersion,
                        snackbarHostState = snackbarHostState,
                    )
                }

                is OnboardingRoute -> {
                    if (onboardingScope != null) {
                        OnboardingNavGraph.entryProvider(
                            route = route,
                            padding = padding,
                            navigator = navigator,
                            snackbarHostState = snackbarHostState,
                            onboardingScope = onboardingScope,
                        )
                    } else {
                        // 온보딩 종료 애니메이션 중에 NPE가 발생하는 것을 방지하기 위해 빈 화면을 반환합니다.
                        entry(route) {}
                    }
                }

                is HomeRoute -> {
                    HomeNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        onRegisterPushNotification = onRegisterPushNotification,
                        onRequestInitialPermissions = onRequestInitialPermissions,
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
