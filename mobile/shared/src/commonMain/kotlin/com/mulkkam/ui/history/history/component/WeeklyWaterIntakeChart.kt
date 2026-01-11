package com.mulkkam.ui.history.history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.ui.designsystem.Gray10
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary10
import com.mulkkam.ui.designsystem.Primary50
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.format
import com.mulkkam.ui.util.extensions.noRippleClickable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.history_week_next
import mulkkam.shared.generated.resources.history_week_prev
import mulkkam.shared.generated.resources.history_week_range
import mulkkam.shared.generated.resources.ic_common_next
import mulkkam.shared.generated.resources.ic_common_prev
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val MONTH_DATE: String = "M월 d일"
private const val FULL_DATE: String = "yyyy년 M월 d일"
private const val WEEK_OFFSET_PREV: Long = -1L
private const val WEEK_OFFSET_NEXT: Long = 1L

@OptIn(ExperimentalTime::class)
@Composable
fun WeeklyWaterIntakeChart(
    weeklyIntakeHistorySummaries: IntakeHistorySummaries,
    onClickDate: (IntakeHistorySummary) -> Unit,
    onClickButton: (Long) -> Unit,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
    currentDate: LocalDate =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date,
    isNotCurrentWeek: Boolean = false,
) {
    Column(
        modifier = modifier.background(White),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_prev),
                contentDescription = stringResource(Res.string.history_week_prev),
                modifier =
                    Modifier
                        .size(40.dp)
                        .noRippleClickable(onClick = { onClickButton(WEEK_OFFSET_PREV) })
                        .padding(5.dp)
                        .align(Alignment.CenterStart),
                tint = Gray400,
            )

            val pattern =
                if (weeklyIntakeHistorySummaries.isCurrentYear(currentDate)) MONTH_DATE else FULL_DATE

            Text(
                text =
                    stringResource(
                        Res.string.history_week_range,
                        weeklyIntakeHistorySummaries.firstDay.format(pattern),
                        weeklyIntakeHistorySummaries.lastDay.format(pattern),
                    ),
                color = Gray400,
                style = MulKkamTheme.typography.title2,
                modifier = Modifier.align(Alignment.Center),
            )

            if (isNotCurrentWeek) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_next),
                    contentDescription = stringResource(Res.string.history_week_next),
                    modifier =
                        Modifier
                            .size(40.dp)
                            .noRippleClickable(onClick = { onClickButton(WEEK_OFFSET_NEXT) })
                            .padding(5.dp)
                            .align(Alignment.CenterEnd),
                    tint = Gray400,
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            weeklyIntakeHistorySummaries.intakeHistorySummaries.forEach {
                val chartModifier =
                    when (it.date) {
                        selectedDate ->
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
                    modifier =
                        chartModifier
                            .noRippleClickable(onClick = { onClickDate(it) })
                            .weight(1f),
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "이번주 기록을 조회할 경우")
@Composable
private fun WeeklyWaterIntakeChartPreview_ThisWeek() {
    MulKkamTheme {
        WeeklyWaterIntakeChart(
            IntakeHistorySummaries(
                intakeHistorySummaries =
                    listOf(
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 27),
                            targetAmount = 1000,
                            totalIntakeAmount = 100,
                            achievementRate = 10f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 28),
                            targetAmount = 1000,
                            totalIntakeAmount = 200,
                            achievementRate = 20f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 29),
                            targetAmount = 1000,
                            totalIntakeAmount = 300,
                            achievementRate = 30f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 30),
                            targetAmount = 1000,
                            totalIntakeAmount = 400,
                            achievementRate = 40f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 31),
                            targetAmount = 1000,
                            totalIntakeAmount = 500,
                            achievementRate = 50f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 11, 1),
                            targetAmount = 1000,
                            totalIntakeAmount = 600,
                            achievementRate = 60f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 11, 2),
                            targetAmount = 1000,
                            totalIntakeAmount = 1000,
                            achievementRate = 100f,
                            intakeHistories = listOf(),
                        ),
                    ),
            ),
            selectedDate = LocalDate(2025, 10, 31),
            onClickDate = { _ -> },
            onClickButton = {},
        )
    }
}

@Preview(showBackground = true, name = "다른 주 기록을 조회할 경우")
@Composable
private fun WeeklyWaterIntakeChartPreview_DifferentWeek() {
    MulKkamTheme {
        WeeklyWaterIntakeChart(
            IntakeHistorySummaries(
                intakeHistorySummaries =
                    listOf(
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 27),
                            targetAmount = 1000,
                            totalIntakeAmount = 100,
                            achievementRate = 10f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 28),
                            targetAmount = 1000,
                            totalIntakeAmount = 200,
                            achievementRate = 20f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 29),
                            targetAmount = 1000,
                            totalIntakeAmount = 300,
                            achievementRate = 30f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 30),
                            targetAmount = 1000,
                            totalIntakeAmount = 400,
                            achievementRate = 40f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 10, 31),
                            targetAmount = 1000,
                            totalIntakeAmount = 500,
                            achievementRate = 50f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 11, 1),
                            targetAmount = 1000,
                            totalIntakeAmount = 600,
                            achievementRate = 60f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2025, 11, 2),
                            targetAmount = 1000,
                            totalIntakeAmount = 1000,
                            achievementRate = 100f,
                            intakeHistories = listOf(),
                        ),
                    ),
            ),
            isNotCurrentWeek = true,
            selectedDate = LocalDate(2025, 11, 10),
            onClickDate = { _ -> },
            onClickButton = {},
        )
    }
}

@Preview(showBackground = true, name = "다른 연도의 기록을 조회할 경우")
@Composable
private fun WeeklyWaterIntakeChartPreview_DifferentYear() {
    MulKkamTheme {
        WeeklyWaterIntakeChart(
            IntakeHistorySummaries(
                intakeHistorySummaries =
                    listOf(
                        IntakeHistorySummary(
                            date = LocalDate(2024, 10, 27),
                            targetAmount = 1000,
                            totalIntakeAmount = 100,
                            achievementRate = 10f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2024, 10, 28),
                            targetAmount = 1000,
                            totalIntakeAmount = 200,
                            achievementRate = 20f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2024, 10, 29),
                            targetAmount = 1000,
                            totalIntakeAmount = 300,
                            achievementRate = 30f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2024, 10, 30),
                            targetAmount = 1000,
                            totalIntakeAmount = 400,
                            achievementRate = 40f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2024, 10, 31),
                            targetAmount = 1000,
                            totalIntakeAmount = 500,
                            achievementRate = 50f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2024, 11, 1),
                            targetAmount = 1000,
                            totalIntakeAmount = 600,
                            achievementRate = 60f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate(2024, 11, 2),
                            targetAmount = 1000,
                            totalIntakeAmount = 1000,
                            achievementRate = 100f,
                            intakeHistories = listOf(),
                        ),
                    ),
            ),
            isNotCurrentWeek = true,
            selectedDate = LocalDate(2025, 11, 10),
            onClickDate = { _ -> },
            onClickButton = {},
        )
    }
}
