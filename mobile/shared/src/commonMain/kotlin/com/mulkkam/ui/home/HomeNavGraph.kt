package com.mulkkam.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.home.encyclopedia.EncyclopediaRoute
import com.mulkkam.ui.home.home.HomeRoute
import com.mulkkam.ui.home.notification.NotificationRoute
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.entry
import com.mulkkam.ui.navigation.HomeRoute as HomeNavRoute

object HomeNavGraph {
    @Composable
    fun entryProvider(
        route: HomeNavRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
        onRegisterPushNotification: (
            onTokenUpdated: (token: String) -> Unit,
            onPermissionUpdated: (isGranted: Boolean) -> Unit,
            onError: (errorMessage: String) -> Unit,
        ) -> Unit,
        onRequestMainPermissions: () -> Unit,
        snackbarHostState: SnackbarHostState,
    ): NavEntry<HomeNavRoute> =
        when (route) {
            is HomeNavRoute.Home -> {
                entry(route) {
                    HomeRoute(
                        padding = padding,
                        navigateToNotification = navigator::navigateToHomeNotification,
                        onNavigateToLogin = navigator::navigateToLogin,
                        onNavigateToCoffeeEncyclopedia = navigator::navigateToEncyclopedia,
                        onRegisterPushNotification = onRegisterPushNotification,
                        onRequestMainPermissions = onRequestMainPermissions,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }

            is HomeNavRoute.Encyclopedia -> {
                entry(route) {
                    EncyclopediaRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                    )
                }
            }

            is HomeNavRoute.Notification -> {
                entry(route) {
                    NotificationRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
        }
}
