package com.mulkkam.ui.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.component.ColoredText
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import java.util.Locale

@Composable
fun HomeProgressOverview(
    nickname: String?,
    streak: Int?,
    achievementRate: Float,
    totalAmount: Int?,
    targetAmount: Int?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (nickname != null && streak != null) {
            ColoredText(
                fullText = stringResource(R.string.home_water_streak_message, nickname, streak),
                highlightedTexts = listOf(nickname, streak.toString()),
                highlightColor = Primary200,
                style = MulKkamTheme.typography.title2,
                color = Gray400,
                modifier = Modifier.padding(start = 24.dp, top = 4.dp),
            )
        }

        Spacer(Modifier.height(46.dp))

        LinearGradientProgress(
            progress = achievementRate.coerceIn(0f, 100f),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .padding(horizontal = 36.dp),
        )

        Spacer(Modifier.height(8.dp))

        if (totalAmount != null && targetAmount != null) {
            val summary =
                stringResource(
                    R.string.home_daily_intake_summary,
                    totalAmount,
                    targetAmount,
                )
            val highlightColor =
                if (targetAmount > totalAmount) {
                    Gray200
                } else {
                    Primary200
                }

            ColoredText(
                fullText = summary,
                highlightedTexts = listOf(String.format(Locale.getDefault(), "%,dml", totalAmount)),
                highlightColor = highlightColor,
                style = MulKkamTheme.typography.title3,
                color = Gray400,
                modifier =
                    Modifier
                        .padding(end = 36.dp)
                        .align(Alignment.End),
            )
        }
    }
}
