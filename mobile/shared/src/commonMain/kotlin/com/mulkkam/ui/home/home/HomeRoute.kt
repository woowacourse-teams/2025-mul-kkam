package com.mulkkam.ui.home.home

import androidx.compose.runtime.Composable

@Composable
fun HomeRoute(
    onNavigateToNotification: () -> Unit,
    onManualDrink: () -> Unit,
) {
    HomeScreen(
        navigateToNotification = onNavigateToNotification,
        onManualDrink = onManualDrink,
    )
}
