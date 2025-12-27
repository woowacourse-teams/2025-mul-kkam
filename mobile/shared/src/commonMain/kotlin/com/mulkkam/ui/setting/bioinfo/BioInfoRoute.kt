package com.mulkkam.ui.setting.bioinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
fun BioInfoRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
) {
    BioInfoScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
    )
}
