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
import com.mulkkam.ui.util.extensions.noRippleClickable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.history_week_next
import mulkkam.shared.generated.resources.history_week_prev
import mulkkam.shared.generated.resources.history_week_range
import mulkkam.shared.generated.resources.ic_common_next
import mulkkam.shared.generated.resources.ic_common_prev
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val WEEK_OFFSET_PREV: Long = -1L
private const val WEEK_OFFSET_NEXT: Long = 1L

@OptIn(ExperimentalTime::class)
@Composable
fun WeeklyWaterIntakeChart(
    weeklyIntakeHistorySummaries: IntakeHistorySummaries,
    onClickDate: (IntakeHistorySummary) -> Unit,
    onClickButton: (Long) -> Unit,
    modifier: Modifier = Modifier,
    currentDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
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

            val dateRangeText =
                formatDateRange(
                    weeklyIntakeHistorySummaries,
                    currentDate,
                )

            Text(
                text =
                    stringResource(
                        Res.string.history_week_range,
                        dateRangeText.first,
                        dateRangeText.second,
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
                    modifier =
                        chartModifier
                            .noRippleClickable(onClick = { onClickDate(it) })
                            .weight(1f),
                )
            }
        }
    }
}

private fun formatDateRange(
    summaries: IntakeHistorySummaries,
    currentDate: LocalDate,
): Pair<String, String> {
    val isCurrentYear = summaries.isCurrentYear(currentDate)
    val firstDay = summaries.firstDay
    val lastDay = summaries.lastDay

    return if (isCurrentYear) {
        "${firstDay.monthNumber}월 ${firstDay.dayOfMonth}일" to
            "${lastDay.monthNumber}월 ${lastDay.dayOfMonth}일"
    } else {
        "${firstDay.year}년 ${firstDay.monthNumber}월 ${firstDay.dayOfMonth}일" to
            "${lastDay.year}년 ${lastDay.monthNumber}월 ${lastDay.dayOfMonth}일"
    }
}
