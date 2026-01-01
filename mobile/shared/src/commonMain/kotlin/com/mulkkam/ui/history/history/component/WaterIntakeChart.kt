package com.mulkkam.ui.history.history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Primary300
import com.mulkkam.ui.designsystem.Primary50
import com.mulkkam.ui.designsystem.Secondary200
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_history_check
import mulkkam.shared.generated.resources.water_chart_date
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

const val ACHIEVEMENT_RATE_MAX: Float = 100f

@Composable
fun WaterIntakeChart(
    intakeHistorySummary: IntakeHistorySummary,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
        ) {
            GradientDonutChart(
                modifier = Modifier.fillMaxSize(),
                progress = intakeHistorySummary.achievementRate,
                backgroundColor = Primary50,
                gradientColors = listOf(Primary200, Primary200),
                strokeWidth = 4.dp,
            )
            when (intakeHistorySummary.achievementRate) {
                ACHIEVEMENT_RATE_MAX -> {
                    Icon(
                        painter = painterResource(Res.drawable.ic_history_check),
                        contentDescription = null,
                        tint = Primary100,
                    )
                }

                else -> {
                    Text(
                        text = intakeHistorySummary.achievementRate.toInt().toString(),
                        color = Gray400,
                        style = MulKkamTheme.typography.label2,
                    )
                }
            }
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = getDayOfWeekDisplayName(intakeHistorySummary.date),
            color = getColorByDate(intakeHistorySummary.date),
            style = MulKkamTheme.typography.title3,
        )
        Text(
            text =
                stringResource(
                    Res.string.water_chart_date,
                    intakeHistorySummary.date.monthNumber,
                    intakeHistorySummary.date.dayOfMonth,
                ),
            color = Gray300,
            style = MulKkamTheme.typography.label2,
        )
    }
}

private fun getColorByDate(date: LocalDate): Color =
    when (date.dayOfWeek) {
        DayOfWeek.SATURDAY -> Primary300
        DayOfWeek.SUNDAY -> Secondary200
        else -> Gray400
    }

private fun getDayOfWeekDisplayName(date: LocalDate): String =
    when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
        DayOfWeek.SUNDAY -> "일"
        else -> ""
    }
