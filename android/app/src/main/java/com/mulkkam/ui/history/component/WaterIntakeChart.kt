package com.mulkkam.ui.history.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Primary300
import com.mulkkam.ui.designsystem.Primary50
import com.mulkkam.ui.designsystem.Secondary200
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

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
                .height(74.dp)
                .width(40.dp)
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
                        painter = painterResource(R.drawable.ic_history_check),
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
            text =
                intakeHistorySummary.date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.KOREAN,
                ),
            color = getColorByDate(intakeHistorySummary.date),
            style = MulKkamTheme.typography.title3,
        )
        Text(
            text =
                stringResource(
                    R.string.water_chart_date,
                    intakeHistorySummary.date.monthValue,
                    intakeHistorySummary.date.dayOfMonth,
                ),
            color = Gray300,
            style = MulKkamTheme.typography.label2,
        )
    }
}

private fun getColorByDate(date: LocalDate): Color {
    val colorResId =
        when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> Primary300
            DayOfWeek.SUNDAY -> Secondary200
            else -> Gray400
        }
    return colorResId
}

@Preview(showBackground = true, name = "토요일 음용량 차트")
@Composable
private fun WaterIntakeChartPreview_Saturday() {
    MulkkamTheme {
        WaterIntakeChart(
            intakeHistorySummary =
                IntakeHistorySummary(
                    date = LocalDate.of(2025, 11, 1),
                    targetAmount = 100,
                    totalIntakeAmount = 50,
                    achievementRate = 50f,
                    intakeHistories = listOf(),
                ),
        )
    }
}

@Preview(showBackground = true, name = "일요일 음용량 차트")
@Composable
private fun WaterIntakeChartPreview_Sunday() {
    MulkkamTheme {
        WaterIntakeChart(
            intakeHistorySummary =
                IntakeHistorySummary(
                    date = LocalDate.of(2025, 11, 2),
                    targetAmount = 100,
                    totalIntakeAmount = 50,
                    achievementRate = 50f,
                    intakeHistories = listOf(),
                ),
        )
    }
}

@Preview(showBackground = true, name = "목표량을 전부 채운 음용량 차트")
@Composable
private fun WaterIntakeChartPreview_FullAchievementRate() {
    MulkkamTheme {
        WaterIntakeChart(
            intakeHistorySummary =
                IntakeHistorySummary(
                    date = LocalDate.of(2025, 10, 30),
                    targetAmount = 100,
                    totalIntakeAmount = 100,
                    achievementRate = 100f,
                    intakeHistories = listOf(),
                ),
        )
    }
}
