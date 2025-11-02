package com.mulkkam.ui.history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.ui.designsystem.Gray10
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary10
import com.mulkkam.ui.designsystem.Primary50
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable
import java.time.LocalDate

@Composable
fun WeeklyWaterIntakeChart(
    intakeHistorySummaries: IntakeHistorySummaries,
    onClickDate: (IntakeHistorySummary) -> Unit,
    modifier: Modifier = Modifier,
    currentDate: LocalDate = LocalDate.now(),
) {
    Row(
        modifier = modifier.background(White),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        intakeHistorySummaries.intakeHistorySummaries.forEach {
            val chartModifier =
                when (it.date) {
                    currentDate ->
                        Modifier
                            .border(1.dp, Primary50, RoundedCornerShape(4.dp))
                            .background(Primary10)

                    else ->
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Gray10)
                }

            WaterIntakeChart(
                intakeHistorySummary = it,
                modifier = chartModifier.noRippleClickable(onClick = { onClickDate(it) }).weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeeklyWaterIntakeChartPreview() {
    MulkkamTheme {
        WeeklyWaterIntakeChart(
            intakeHistorySummaries =
                IntakeHistorySummaries(
                    intakeHistorySummaries =
                        listOf(
                            IntakeHistorySummary(
                                date = LocalDate.of(2025, 10, 27),
                                targetAmount = 1000,
                                totalIntakeAmount = 100,
                                achievementRate = 10f,
                                intakeHistories = listOf(),
                            ),
                            IntakeHistorySummary(
                                date = LocalDate.of(2025, 10, 28),
                                targetAmount = 1000,
                                totalIntakeAmount = 200,
                                achievementRate = 20f,
                                intakeHistories = listOf(),
                            ),
                            IntakeHistorySummary(
                                date = LocalDate.of(2025, 10, 29),
                                targetAmount = 1000,
                                totalIntakeAmount = 300,
                                achievementRate = 30f,
                                intakeHistories = listOf(),
                            ),
                            IntakeHistorySummary(
                                date = LocalDate.of(2025, 10, 30),
                                targetAmount = 1000,
                                totalIntakeAmount = 400,
                                achievementRate = 40f,
                                intakeHistories = listOf(),
                            ),
                            IntakeHistorySummary(
                                date = LocalDate.of(2025, 10, 31),
                                targetAmount = 1000,
                                totalIntakeAmount = 500,
                                achievementRate = 50f,
                                intakeHistories = listOf(),
                            ),
                            IntakeHistorySummary(
                                date = LocalDate.of(2025, 11, 1),
                                targetAmount = 1000,
                                totalIntakeAmount = 600,
                                achievementRate = 60f,
                                intakeHistories = listOf(),
                            ),
                            IntakeHistorySummary(
                                date = LocalDate.of(2025, 11, 2),
                                targetAmount = 1000,
                                totalIntakeAmount = 1000,
                                achievementRate = 100f,
                                intakeHistories = listOf(),
                            ),
                        ),
                ),
            onClickDate = { _ -> },
        )
    }
}
