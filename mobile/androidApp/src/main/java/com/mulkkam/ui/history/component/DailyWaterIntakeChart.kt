package com.mulkkam.ui.history.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.domain.model.intake.WaterIntakeState
import com.mulkkam.ui.component.ColoredText
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val INTAKE_AMOUNT_EMPTY: Int = 0

@Composable
fun DailyWaterIntakeChart(
    dailyIntakeHistory: IntakeHistorySummary,
    waterIntakeState: WaterIntakeState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val date = dailyIntakeHistory.date

    Column(
        modifier = modifier.background(White),
    ) {
        Box(
            modifier =
                Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart,
        ) {
            if (waterIntakeState is WaterIntakeState.Present) {
                Text(
                    text = stringResource(R.string.history_today_label),
                    color = Secondary200,
                    style = MulKkamTheme.typography.label2,
                )
            }
        }

        val formattedDate = date.toJavaLocalDate().format(getDateFormatter(context))
        ColoredText(
            fullText =
                stringResource(
                    R.string.history_daily_chart_label,
                    formattedDate,
                ),
            highlightedTexts = listOf(formattedDate),
            highlightColor = Primary200,
            style = MulKkamTheme.typography.title1,
            color = Black,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            GradientDonutChart(
                progress = dailyIntakeHistory.achievementRate,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 20.dp,
            )

            Image(
                painter = painterResource(getCharacterImage(waterIntakeState)),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
            )
        }

        val formattedIntake =
            String.format(Locale.US, "%,dml", dailyIntakeHistory.totalIntakeAmount)
        val summaryColor =
            if (dailyIntakeHistory.targetAmount > dailyIntakeHistory.totalIntakeAmount ||
                dailyIntakeHistory.totalIntakeAmount == INTAKE_AMOUNT_EMPTY
            ) {
                Gray200
            } else {
                Primary200
            }
        ColoredText(
            fullText =
                stringResource(
                    R.string.history_daily_intake_summary,
                    dailyIntakeHistory.totalIntakeAmount,
                    dailyIntakeHistory.targetAmount,
                ),
            highlightedTexts = listOf(formattedIntake),
            highlightColor = summaryColor,
            style = MulKkamTheme.typography.title2,
            color = Black,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 18.dp),
        )
    }
}

private fun getDateFormatter(context: Context): DateTimeFormatter =
    DateTimeFormatter.ofPattern(context.getString(R.string.history_date_with_day_pattern), Locale.getDefault())

private fun getCharacterImage(waterIntakeState: WaterIntakeState) =
    when (waterIntakeState) {
        is WaterIntakeState.Past.NoRecord -> R.drawable.img_crying_character
        is WaterIntakeState.Past.Full -> R.drawable.img_history_character
        is WaterIntakeState.Present.Full -> R.drawable.img_history_character
        is WaterIntakeState.Future -> R.drawable.img_history_sleeping_character
        else -> R.drawable.img_history_character
    }

@Preview(showBackground = true, name = "오늘 목표량을 다 채운 경우")
@Composable
private fun DailyWaterIntakeChartPreview_PresentFull() {
    MulKkamTheme {
        DailyWaterIntakeChart(
            dailyIntakeHistory =
                IntakeHistorySummary(
                    date = LocalDate(2025, 10, 31),
                    targetAmount = 1000,
                    totalIntakeAmount = 1000,
                    achievementRate = 100f,
                    intakeHistories = listOf(),
                ),
            waterIntakeState = WaterIntakeState.Present.Full,
        )
    }
}

@Preview(showBackground = true, name = "오늘 목표량을 다 채우지 않은 경우")
@Composable
private fun DailyWaterIntakeChartPreview_PresentNotFull() {
    MulKkamTheme {
        DailyWaterIntakeChart(
            dailyIntakeHistory =
                IntakeHistorySummary(
                    date = LocalDate(2025, 10, 31),
                    targetAmount = 1000,
                    totalIntakeAmount = 500,
                    achievementRate = 50f,
                    intakeHistories = listOf(),
                ),
            waterIntakeState = WaterIntakeState.Present.NotFull,
        )
    }
}

@Preview(showBackground = true, name = "과거 기록이 없는 경우")
@Composable
private fun DailyWaterIntakeChartPreview_PastNoRecord() {
    MulKkamTheme {
        DailyWaterIntakeChart(
            dailyIntakeHistory =
                IntakeHistorySummary(
                    date = LocalDate(2025, 10, 20),
                    targetAmount = 1000,
                    totalIntakeAmount = 0,
                    achievementRate = 0f,
                    intakeHistories = listOf(),
                ),
            waterIntakeState = WaterIntakeState.Past.NoRecord,
        )
    }
}

@Preview(showBackground = true, name = "과거 목표량을 다 채우지 않은 경우")
@Composable
private fun DailyWaterIntakeChartPreview_PastPartial() {
    MulKkamTheme {
        DailyWaterIntakeChart(
            dailyIntakeHistory =
                IntakeHistorySummary(
                    date = LocalDate(2025, 10, 20),
                    targetAmount = 1000,
                    totalIntakeAmount = 500,
                    achievementRate = 50f,
                    intakeHistories = listOf(),
                ),
            waterIntakeState = WaterIntakeState.Past.Partial,
        )
    }
}

@Preview(showBackground = true, name = "과거 목표량을 다 채운 경우")
@Composable
private fun DailyWaterIntakeChartPreview_PastFull() {
    MulKkamTheme {
        DailyWaterIntakeChart(
            dailyIntakeHistory =
                IntakeHistorySummary(
                    date = LocalDate(2025, 10, 20),
                    targetAmount = 1000,
                    totalIntakeAmount = 1000,
                    achievementRate = 100f,
                    intakeHistories = listOf(),
                ),
            waterIntakeState = WaterIntakeState.Past.Full,
        )
    }
}

@Preview(showBackground = true, name = "미래인 경우")
@Composable
private fun DailyWaterIntakeChartPreview_Future() {
    MulKkamTheme {
        DailyWaterIntakeChart(
            dailyIntakeHistory =
                IntakeHistorySummary(
                    date = LocalDate(2025, 11, 3),
                    targetAmount = 1000,
                    totalIntakeAmount = 0,
                    achievementRate = 0f,
                    intakeHistories = listOf(),
                ),
            waterIntakeState = WaterIntakeState.Future,
        )
    }
}
