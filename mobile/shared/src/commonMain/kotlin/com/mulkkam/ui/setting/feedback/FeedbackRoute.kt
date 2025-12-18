package com.mulkkam.ui.setting.feedback

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun FeedbackRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    FeedbackScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
