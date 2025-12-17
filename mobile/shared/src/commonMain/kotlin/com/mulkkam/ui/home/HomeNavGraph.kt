package com.mulkkam.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.home.encyclopedia.EncyclopediaScreen
import com.mulkkam.ui.home.home.HomeScreen
import com.mulkkam.ui.home.notification.NotificationScreen
import com.mulkkam.ui.navigation.HomeRoute
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.entry

object HomeNavGraph {
    @Composable
    fun entryProvider(
        route: HomeRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
    ): NavEntry<HomeRoute> =
        when (route) {
            is HomeRoute.Home -> {
                entry(route) {
                    HomeScreen(
                        padding = padding,
                        onNavigateToEncyclopedia = navigator::navigateToEncyclopedia,
                        onNavigateToNotification = navigator::navigateToHomeNotification,
                    )
                }
            }

            is HomeRoute.Encyclopedia -> {
                entry(route) {
                    EncyclopediaScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }

            is HomeRoute.Notification -> {
                entry(route) {
                    NotificationScreen(
                        padding = padding,
                        onNavigateBack = navigator::popBackStack,
                    )
                }
            }
        }
}
