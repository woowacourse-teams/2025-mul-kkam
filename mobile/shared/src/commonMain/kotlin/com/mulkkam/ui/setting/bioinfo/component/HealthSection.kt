package com.mulkkam.ui.setting.bioinfo.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun HealthSection(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
