package com.mulkkam.ui.home.home.component

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.cups.Cups
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.home_drink_manual
import mulkkam.shared.generated.resources.ic_manual_drink
import mulkkam.shared.generated.resources.intake_unit_ml
import org.jetbrains.compose.resources.stringResource

@Composable
fun DrinkMenu(
    visible: Boolean,
    cups: Cups?,
    onSelectCup: (Long) -> Unit,
    onManual: () -> Unit,
) {
    val elevationOffset: Int = 12
    val itemSize: Dp = 56.dp

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.offset(x = (elevationOffset).dp, y = (elevationOffset).dp),
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
    ) {
        Column(
            modifier = Modifier.padding(elevationOffset.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            DrinkMenuItem(
                label = stringResource(Res.string.home_drink_manual),
                icon = {
                    RoundIconButton(
                        iconRes = Res.drawable.ic_manual_drink,
                        contentDescription = null,
                        onClick = onManual,
                        size = itemSize,
                    )
                },
                onClick = onManual,
            )
            cups?.cups?.reversed()?.forEach { cup ->
                DrinkMenuItem(
                    label = cup.name.value,
                    icon = {
                        DrinkCupOption(
                            emojiUrl = cup.emoji.cupEmojiUrl,
                            label = stringResource(Res.string.intake_unit_ml, cup.amount.value),
                            onClick = { onSelectCup(cup.id) },
                            size = itemSize,
                        )
                    },
                    onClick = { onSelectCup(cup.id) },
                )
            }
        }
    }
}
