package com.mulkkam.ui.history.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.mulkkam.R
import com.mulkkam.domain.model.intake.IntakeHistory
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.component.NetworkImage
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.util.ImageShape
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormatterWithMinutes = DateTimeFormatter.ofPattern("a h시 m분", Locale.KOREA)
private val timeFormatterWithoutMinutes = DateTimeFormatter.ofPattern("a h시", Locale.KOREA)

@Composable
fun IntakeHistoryItem(
    intakeHistory: IntakeHistory,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp),
    ) {
        NetworkImage(
            url = intakeHistory.cupEmojiUrl,
            modifier = Modifier.size(24.dp),
            shape = ImageShape.Circle,
        )

        Text(
            text =
                if (intakeHistory.dateTime.minute == 0) {
                    intakeHistory.dateTime.format(timeFormatterWithoutMinutes)
                } else {
                    intakeHistory.dateTime.format(timeFormatterWithMinutes)
                },
            style = MulKkamTheme.typography.body4,
            color = Black,
            modifier =
                Modifier
                    .padding(vertical = 14.dp)
                    .padding(start = 12.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.history_intake_amount, intakeHistory.intakeAmount),
            style = MulKkamTheme.typography.title2,
            color = Color(intakeHistory.intakeType.toColorHex().toColorInt()),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IntakeHistoryItemPreview() {
    MulkkamTheme {
        IntakeHistoryItem(
            intakeHistory =
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime.of(12, 31),
                    intakeAmount = 350,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "url",
                ),
        )
    }
}
