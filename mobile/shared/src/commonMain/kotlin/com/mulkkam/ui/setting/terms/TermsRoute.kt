package com.mulkkam.ui.setting.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun TermsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    TermsScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
