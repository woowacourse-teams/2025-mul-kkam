package com.mulkkam.ui.home.encyclopedia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
expect fun EncyclopediaRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
)
