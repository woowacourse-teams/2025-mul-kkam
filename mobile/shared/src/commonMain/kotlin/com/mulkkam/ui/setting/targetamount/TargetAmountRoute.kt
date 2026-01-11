package com.mulkkam.ui.setting.targetamount

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun TargetAmountRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    TargetAmountScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
    )
}
