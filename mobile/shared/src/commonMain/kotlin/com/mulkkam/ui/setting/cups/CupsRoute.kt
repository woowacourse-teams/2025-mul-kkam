package com.mulkkam.ui.setting.cups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun CupsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    CupsScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
