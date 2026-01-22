package com.mulkkam.ui.home.encyclopedia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun EncyclopediaRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
) {
    EncyclopediaScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
