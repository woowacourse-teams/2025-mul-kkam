package com.mulkkam.ui.history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.util.extensions.noRippleClickable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val FORMATTER_MONTH_DATE: DateTimeFormatter =
    DateTimeFormatter.ofPattern("M월 d일")
private val FORMATTER_FULL_DATE: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy년 M월 d일")

private const val WEEK_OFFSET_PREV: Long = -1L
private const val WEEK_OFFSET_NEXT: Long = 1L

@Composable
fun HistoryTopAppBar(
    weeklyIntakeHistorySummaries: IntakeHistorySummaries,
    isNotCurrentWeek: Boolean,
    onClickButton: (Long) -> Unit,
) {
    Column {
        Text(
            text = "기록",
            color = Black,
            style = MulKkamTheme.typography.headline1,
            modifier = Modifier.padding(top = 28.dp, start = 24.dp),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_common_prev),
                contentDescription = stringResource(R.string.history_week_prev),
                modifier =
                    Modifier
                        .size(40.dp)
                        .noRippleClickable(onClick = { onClickButton(WEEK_OFFSET_PREV) })
                        .padding(5.dp)
                        .align(Alignment.CenterStart),
                tint = Gray400,
            )

            val formatter =
                if (weeklyIntakeHistorySummaries.isCurrentYear()) FORMATTER_MONTH_DATE else FORMATTER_FULL_DATE

            Text(
                text =
                    stringResource(
                        R.string.history_week_range,
                        weeklyIntakeHistorySummaries.firstDay.format(formatter),
                        weeklyIntakeHistorySummaries.lastDay.format(formatter),
                    ),
                color = Gray400,
                style = MulKkamTheme.typography.title2,
                modifier = Modifier.align(Alignment.Center),
            )

            if (isNotCurrentWeek) {
                Icon(
                    painter = painterResource(R.drawable.ic_common_next),
                    contentDescription = stringResource(R.string.history_week_next),
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
    }
}

@Preview(showBackground = true, name = "이번주 기록을 조회할 경우")
@Composable
private fun HistoryTopAppBarPreview_ThisWeek() {
    MulkkamTheme {
        HistoryTopAppBar(
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
            isNotCurrentWeek = false,
            onClickButton = {},
        )
    }
}

@Preview(showBackground = true, name = "다른 주 기록을 조회할 경우")
@Composable
private fun HistoryTopAppBarPreview_DiffenttWeek() {
    MulkkamTheme {
        HistoryTopAppBar(
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
            isNotCurrentWeek = true,
            onClickButton = {},
        )
    }
}

@Preview(showBackground = true, name = "다른 연도의 기록을 조회할 경우")
@Composable
private fun HistoryTopAppBarPreview_DifferentYear() {
    MulkkamTheme {
        HistoryTopAppBar(
            IntakeHistorySummaries(
                intakeHistorySummaries =
                    listOf(
                        IntakeHistorySummary(
                            date = LocalDate.of(2024, 10, 27),
                            targetAmount = 1000,
                            totalIntakeAmount = 100,
                            achievementRate = 10f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate.of(2024, 10, 28),
                            targetAmount = 1000,
                            totalIntakeAmount = 200,
                            achievementRate = 20f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate.of(2024, 10, 29),
                            targetAmount = 1000,
                            totalIntakeAmount = 300,
                            achievementRate = 30f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate.of(2024, 10, 30),
                            targetAmount = 1000,
                            totalIntakeAmount = 400,
                            achievementRate = 40f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate.of(2024, 10, 31),
                            targetAmount = 1000,
                            totalIntakeAmount = 500,
                            achievementRate = 50f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate.of(2024, 11, 1),
                            targetAmount = 1000,
                            totalIntakeAmount = 600,
                            achievementRate = 60f,
                            intakeHistories = listOf(),
                        ),
                        IntakeHistorySummary(
                            date = LocalDate.of(2024, 11, 2),
                            targetAmount = 1000,
                            totalIntakeAmount = 1000,
                            achievementRate = 100f,
                            intakeHistories = listOf(),
                        ),
                    ),
            ),
            isNotCurrentWeek = true,
            onClickButton = {},
        )
    }
}
