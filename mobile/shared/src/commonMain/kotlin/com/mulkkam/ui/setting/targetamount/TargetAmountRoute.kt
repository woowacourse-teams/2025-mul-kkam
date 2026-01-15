package com.mulkkam.ui.setting.targetamount

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun TargetAmountRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
) {
    TargetAmountScreen(
        padding = padding,
        navigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
