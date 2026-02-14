package com.mulkkam.ui.setting.targetamount

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun TargetAmountRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    TargetAmountScreen(
        padding =
            PaddingValues(
                start =
                    padding.calculateStartPadding(
                        layoutDirection = LocalLayoutDirection.current,
                    ),
                top = 0.dp,
                end =
                    padding.calculateEndPadding(
                        layoutDirection = LocalLayoutDirection.current,
                    ),
                bottom = padding.calculateBottomPadding(),
            ),
        navigateToBack = onNavigateToBack,
        snackbarHostState = snackbarHostState,
    )
}
