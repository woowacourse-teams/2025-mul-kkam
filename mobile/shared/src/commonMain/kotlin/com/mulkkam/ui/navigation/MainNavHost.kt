package com.mulkkam.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.ui.auth.AuthNavGraph
import com.mulkkam.ui.friends.FriendsNavGraph
import com.mulkkam.ui.history.HistoryNavGraph
import com.mulkkam.ui.home.HomeNavGraph
import com.mulkkam.ui.main.MainScreen
import com.mulkkam.ui.onboarding.OnboardingNavGraph
import com.mulkkam.ui.setting.SettingNavGraph

@Composable
fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
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
                    )
                }

                is OnboardingRoute -> {
                    OnboardingNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is MainRoute -> {
                    entry(route) {
                        MainScreen(navigator = navigator)
                    }
                }

                is HomeRoute -> {
                    HomeNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is HistoryRoute -> {
                    HistoryNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is FriendsRoute -> {
                    FriendsNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                is SettingRoute -> {
                    SettingNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                    )
                }

                else -> {
                    entry(route) {}
                }
            }
        },
    )
}
