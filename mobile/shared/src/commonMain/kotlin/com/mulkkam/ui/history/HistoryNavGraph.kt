package com.mulkkam.ui.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.navigation.HistoryRoute
import com.mulkkam.ui.navigation.MainNavigator

object HistoryNavGraph {
    @Composable
    fun entryProvider(
        route: com.mulkkam.ui.navigation.HistoryRoute,
        padding: PaddingValues,
        navigator: com.mulkkam.ui.navigation.MainNavigator,
    ): com.mulkkam.ui.core.NavEntry<com.mulkkam.ui.navigation.HistoryRoute> =
        when (route) {
            is com.mulkkam.ui.navigation.HistoryRoute.History -> {
                _root_ide_package_.com.mulkkam.ui.core.entry(route) {
                    _root_ide_package_.com.mulkkam.ui.history.history
                        .HistoryScreen(padding = padding)
                }
            }
        }
}
