package com.mulkkam.ui.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mulkkam.ui.history.history.HistoryRoute
import com.mulkkam.ui.navigation.HistoryRoute
import com.mulkkam.ui.navigation.MainNavigator
import com.mulkkam.ui.navigation.NavEntry
import com.mulkkam.ui.navigation.entry

object HistoryNavGraph {
    @Composable
    fun entryProvider(
        route: HistoryRoute,
        padding: PaddingValues,
        navigator: MainNavigator,
        snackbarHostState: SnackbarHostState,
    ): NavEntry<HistoryRoute> =
        when (route) {
            is HistoryRoute.History -> {
                entry(route) {
                    HistoryRoute(padding = padding, snackbarHostState = snackbarHostState)
                }
            }
        }
}
