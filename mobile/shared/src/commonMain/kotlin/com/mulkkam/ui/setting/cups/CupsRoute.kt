package com.mulkkam.ui.setting.cups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun CupsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    CupsScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
