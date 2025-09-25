package com.mulkkam.ui.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupEmoji
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.designsystem.MulkkamTheme

@Composable
fun DrinkMenu(
    visible: Boolean,
    cups: Cups?,
    onSelectCup: (Long) -> Unit,
    onManual: () -> Unit,
) {
    val elevationOffset: Int = 12
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.offset(x = (elevationOffset).dp, y = (elevationOffset).dp),
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Companion.Bottom),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Companion.Bottom),
    ) {
        Column(
            modifier = Modifier.padding(elevationOffset.dp),
            horizontalAlignment = Alignment.Companion.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DrinkMenuItem(
                label = stringResource(id = R.string.home_drink_manual),
                icon = {
                    RoundIconButton(
                        iconRes = R.drawable.ic_manual_drink,
                        contentDescription = null,
                        onClick = onManual,
                    )
                },
                onClick = onManual,
            )
            cups?.cups?.reversed()?.forEach { cup ->
                DrinkMenuItem(
                    label = cup.name.value,
                    icon = {
                        CupEmoji(
                            emojiUrl = cup.emoji.cupEmojiUrl,
                            label = stringResource(R.string.intake_unit_ml, cup.amount),
                            onClick = { onSelectCup(cup.id) },
                        )
                    },
                    onClick = { onSelectCup(cup.id) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrinkMenuPreview() {
    MulkkamTheme {
        val cups =
            Cups(
                listOf(
                    Cup(1, CupName("스타벅스 텀블러"), CupAmount(355), 1, IntakeType.WATER, CupEmoji(1L, "https://example.com/1")),
                    Cup(2, CupName("종이컵"), CupAmount(200), 2, IntakeType.WATER, CupEmoji(2L, "https://example.com/2")),
                    Cup(3, CupName("머그컵"), CupAmount(250), 3, IntakeType.COFFEE, CupEmoji(3L, "https://example.com/3")),
                ),
            )
        DrinkMenu(
            visible = true,
            cups = cups,
            onSelectCup = {},
            onManual = {},
        )
    }
}
