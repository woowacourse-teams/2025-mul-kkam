package com.mulkkam.ui.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.mulkkam.R

@Composable
fun DrinkActionButton(
    expanded: Boolean,
    onToggle: () -> Unit,
    size: Dp,
) {
    RoundIconButton(
        iconRes = if (!expanded) R.drawable.ic_home_drink else R.drawable.ic_home_close,
        contentDescription = null,
        size = size,
        onClick = onToggle,
    )
}
