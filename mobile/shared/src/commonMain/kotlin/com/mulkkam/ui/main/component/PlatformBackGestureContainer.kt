package com.mulkkam.ui.main.component

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformBackGestureContainer(
    enabled: Boolean,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
)
