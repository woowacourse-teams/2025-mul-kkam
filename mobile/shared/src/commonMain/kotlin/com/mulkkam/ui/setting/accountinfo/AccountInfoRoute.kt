package com.mulkkam.ui.setting.accountinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun AccountInfoRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    AccountInfoScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
