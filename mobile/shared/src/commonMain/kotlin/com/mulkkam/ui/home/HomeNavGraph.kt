package com.mulkkam.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.HomeRoute
import com.mulkkam.ui.navigation.MainNavigator

object HomeNavGraph {
    @Composable
    fun entryProvider(
        route: com.mulkkam.ui.navigation.HomeRoute,
        padding: PaddingValues,
        navigator: com.mulkkam.ui.navigation.MainNavigator,
    ): com.mulkkam.ui.core.NavEntry<com.mulkkam.ui.navigation.HomeRoute> =
        when (route) {
            is com.mulkkam.ui.navigation.HomeRoute.Home -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.home.home.HomeScreen(
                        padding = padding,
                        onNavigateToEncyclopedia = com.mulkkam.ui.navigation.MainNavigator::navigateToEncyclopedia,
                        onNavigateToNotification = com.mulkkam.ui.navigation.MainNavigator::navigateToHomeNotification,
                    )
                }
            }

            is com.mulkkam.ui.navigation.HomeRoute.Encyclopedia -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.home.encyclopedia.EncyclopediaScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }

            is com.mulkkam.ui.navigation.HomeRoute.Notification -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.home.notification.NotificationScreen(
                        padding = padding,
                        onNavigateBack = com.mulkkam.ui.navigation.MainNavigator::popBackStack,
                    )
                }
            }
        }
}
