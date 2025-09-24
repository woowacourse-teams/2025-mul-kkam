package com.mulkkam.ui.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.cups.Cups

@Composable
fun DrinkMenu(
    visible: Boolean,
    cups: Cups?,
    onSelectCup: (Long) -> Unit,
    onManual: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Companion.Bottom),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Companion.Bottom),
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            cups?.cups?.forEach { cup ->
                DrinkMenuItem(
                    label = cup.name.value,
                    icon = {
                        CupEmoji(
                            emojiUrl = cup.emoji.cupEmojiUrl,
                            label = "${cup.amount.value}ml",
                        )
                    },
                    onClick = { onSelectCup(cup.id) },
                )
            }

            DrinkMenuItem(
                label = stringResource(id = R.string.home_drink_manual),
                icon = {
                    RoundIconButton(
                        iconRes = R.drawable.ic_manual_drink,
                        contentDescription = null,
                    )
                },
                onClick = onManual,
            )
        }
    }
}
