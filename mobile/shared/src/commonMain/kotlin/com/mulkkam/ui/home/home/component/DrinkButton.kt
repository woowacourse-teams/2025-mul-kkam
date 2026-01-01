package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.cups.Cups
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_home_close
import mulkkam.shared.generated.resources.ic_home_drink

@Composable
fun DrinkButton(
    cups: Cups?,
    onSelectCup: (Long) -> Unit,
    onManual: () -> Unit,
    modifier: Modifier = Modifier,
    mainButtonSize: Dp = 72.dp,
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(cups) {
        expanded = false
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            DrinkMenu(
                visible = expanded,
                cups = cups,
                onSelectCup = {
                    expanded = false
                    onSelectCup(it)
                },
                onManual = {
                    expanded = false
                    onManual()
                },
            )
            RoundIconButton(
                iconRes = if (!expanded) Res.drawable.ic_home_drink else Res.drawable.ic_home_close,
                contentDescription = null,
                size = mainButtonSize,
                onClick = { expanded = !expanded },
            )
        }
    }
}
