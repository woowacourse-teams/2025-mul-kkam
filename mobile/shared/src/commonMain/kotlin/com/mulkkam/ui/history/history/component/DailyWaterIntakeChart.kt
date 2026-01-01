package com.mulkkam.ui.history.history.component

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
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.domain.model.intake.WaterIntakeState
import com.mulkkam.ui.component.ColoredText
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.history_daily_chart_label
import mulkkam.shared.generated.resources.history_daily_intake_summary
import mulkkam.shared.generated.resources.history_today_label
import mulkkam.shared.generated.resources.img_crying_character
import mulkkam.shared.generated.resources.img_history_character
import mulkkam.shared.generated.resources.img_history_sleeping_character
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val INTAKE_AMOUNT_EMPTY: Int = 0

@Composable
fun DailyWaterIntakeChart(
    dailyIntakeHistory: IntakeHistorySummary,
    waterIntakeState: WaterIntakeState,
    modifier: Modifier = Modifier,
) {
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
                    text = stringResource(Res.string.history_today_label),
                    color = Secondary200,
                    style = MulKkamTheme.typography.label2,
                )
            }
        }

        val formattedDate = "${date.monthNumber}월 ${date.dayOfMonth}일 (${getDayOfWeekShort(date)})"
        ColoredText(
            fullText =
                stringResource(
                    Res.string.history_daily_chart_label,
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

        val formattedIntake = "${dailyIntakeHistory.totalIntakeAmount}ml"
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
                    Res.string.history_daily_intake_summary,
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

private fun getDayOfWeekShort(date: kotlinx.datetime.LocalDate): String =
    when (date.dayOfWeek) {
        kotlinx.datetime.DayOfWeek.MONDAY -> "월"
        kotlinx.datetime.DayOfWeek.TUESDAY -> "화"
        kotlinx.datetime.DayOfWeek.WEDNESDAY -> "수"
        kotlinx.datetime.DayOfWeek.THURSDAY -> "목"
        kotlinx.datetime.DayOfWeek.FRIDAY -> "금"
        kotlinx.datetime.DayOfWeek.SATURDAY -> "토"
        kotlinx.datetime.DayOfWeek.SUNDAY -> "일"
        else -> ""
    }

private fun getCharacterImage(waterIntakeState: WaterIntakeState): DrawableResource =
    when (waterIntakeState) {
        is WaterIntakeState.Past.NoRecord -> Res.drawable.img_crying_character
        is WaterIntakeState.Past.Full -> Res.drawable.img_history_character
        is WaterIntakeState.Present.Full -> Res.drawable.img_history_character
        is WaterIntakeState.Future -> Res.drawable.img_history_sleeping_character
        else -> Res.drawable.img_history_character
    }
