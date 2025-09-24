package com.mulkkam.ui.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.cups.Cups

@Composable
fun DrinkButton(
    cups: Cups?,
    onSelectCup: (Long) -> Unit,
    onManual: () -> Unit,
    modifier: Modifier = Modifier,
    edgePadding: Dp = 12.dp,
    mainButtonSize: Dp = 80.dp,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.padding(end = edgePadding, bottom = edgePadding),
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

            DrinkActionButton(
                expanded = expanded,
                onToggle = { expanded = !expanded },
                size = mainButtonSize,
            )
        }
    }
}
