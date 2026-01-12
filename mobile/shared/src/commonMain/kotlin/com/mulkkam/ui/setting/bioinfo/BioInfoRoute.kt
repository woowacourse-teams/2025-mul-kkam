package com.mulkkam.ui.setting.bioinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun BioInfoRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
) {
    BioInfoScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
