package com.mulkkam.ui.home.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun HomeRoute(
    padding: PaddingValues,
    onNavigateToEncyclopedia: () -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    HomeScreen(
        padding = padding,
        onNavigateToEncyclopedia = onNavigateToEncyclopedia,
        onNavigateToNotification = onNavigateToNotification,
    )
}
