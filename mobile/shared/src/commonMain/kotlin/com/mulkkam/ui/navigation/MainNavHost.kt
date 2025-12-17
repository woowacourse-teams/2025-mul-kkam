package com.mulkkam.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.ui.app.AppNavGraph
import com.mulkkam.ui.friends.FriendsNavGraph
import com.mulkkam.ui.history.HistoryNavGraph
import com.mulkkam.ui.home.HomeNavGraph
import com.mulkkam.ui.onboarding.OnboardingNavGraph
import com.mulkkam.ui.setting.SettingNavGraph

@Composable
fun MainNavHost(
    navigator: com.mulkkam.ui.navigation.MainNavigator,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    _root_ide_package_.com.mulkkam.ui.core.NavDisplay(
        backStack = navigator.backStack,
        entryProvider = { route ->
            when (route) {
                is com.mulkkam.ui.navigation.AppRoute -> {
                    _root_ide_package_.com.mulkkam.ui.app.AppNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is com.mulkkam.ui.navigation.OnboardingRoute -> {
                    _root_ide_package_.com.mulkkam.ui.onboarding.OnboardingNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is com.mulkkam.ui.navigation.HomeRoute -> {
                    _root_ide_package_.com.mulkkam.ui.home.HomeNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is com.mulkkam.ui.navigation.HistoryRoute -> {
                    _root_ide_package_.com.mulkkam.ui.history.HistoryNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is com.mulkkam.ui.navigation.FriendsRoute -> {
                    _root_ide_package_.com.mulkkam.ui.friends.FriendsNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is com.mulkkam.ui.navigation.SettingRoute -> {
                    _root_ide_package_.com.mulkkam.ui.setting.SettingNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                else -> {
                    _root_ide_package_.com.mulkkam.ui.core
                        .entry(route) {}
                }
            }
        },
    )
}
