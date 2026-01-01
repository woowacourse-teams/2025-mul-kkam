package com.mulkkam.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.home.encyclopedia.EncyclopediaRoute
import com.mulkkam.ui.home.home.HomeRoute
import com.mulkkam.ui.home.notification.NotificationRoute
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
                    HomeRoute(
                        onNavigateToNotification = navigator::navigateToHomeNotification,
                        onManualDrink = {}, // TODO: ManualDrink BottomSheet 연결
                    )
                }
            }

            is HomeRoute.Encyclopedia -> {
                entry(route) {
                    EncyclopediaRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                    )
                }
            }

            is HomeRoute.Notification -> {
                entry(route) {
                    NotificationRoute(
                        padding = padding,
                        onNavigateToBack = navigator::popBackStack,
                    )
                }
            }
        }
}
