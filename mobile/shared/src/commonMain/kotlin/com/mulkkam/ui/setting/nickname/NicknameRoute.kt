package com.mulkkam.ui.setting.nickname

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun NicknameRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    NicknameScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
