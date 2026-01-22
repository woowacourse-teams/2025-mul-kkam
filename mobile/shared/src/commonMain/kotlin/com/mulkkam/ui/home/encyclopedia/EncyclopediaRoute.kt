package com.mulkkam.ui.home.encyclopedia

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp

@Composable
expect fun EncyclopediaRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
)
