package com.mulkkam.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun IntakeTypeChips(
    selectedIntakeType: IntakeType,
    onSelect: (IntakeType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IntakeType.entries
            .filter { it != IntakeType.UNKNOWN }
            .forEach { intakeType ->
                val isSelected = selectedIntakeType == intakeType
                Box(
                    modifier =
                        Modifier
                            .noRippleClickable(onClick = { onSelect(intakeType) })
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                            .background(
                                color = if (isSelected) Color(intakeType.toColorHex().toColorInt()) else Transparent,
                                shape = RoundedCornerShape(1000.dp),
                            ).border(
                                width = 1.dp,
                                color = if (isSelected) Transparent else Color(intakeType.toColorHex().toColorInt()),
                                shape = RoundedCornerShape(1000.dp),
                            ).padding(horizontal = 18.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = intakeType.toLabel(),
                        style = MulKkamTheme.typography.title3,
                        color = if (isSelected) White else Color(intakeType.toColorHex().toColorInt()),
                    )
                }
            }
    }
}

@Preview(showBackground = true)
@Composable
private fun IntakeTypeChipsPreview() {
    MulkkamTheme {
        IntakeTypeChips(
            selectedIntakeType = IntakeType.WATER,
            onSelect = {},
        )
    }
}
