package com.mulkkam.ui.history.history.component

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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.intake.IntakeHistory
import com.mulkkam.ui.component.NetworkImage
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.ImageShape
import com.mulkkam.ui.util.extensions.format
import com.mulkkam.ui.util.extensions.toColorInt
import com.mulkkam.ui.util.extensions.toCommaSeparated
import kotlinx.datetime.LocalTime
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.history_intake_amount
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val TIME_WITH_MINUTES = "a h시 m분"
private const val TIME_WITHOUT_MINUTES = "a h시"

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

        val pattern =
            if (intakeHistory.dateTime.minute == 0) TIME_WITHOUT_MINUTES else TIME_WITH_MINUTES

        Text(
            text = intakeHistory.dateTime.format(pattern),
            style = MulKkamTheme.typography.body4,
            color = Black,
            modifier =
                Modifier
                    .padding(vertical = 14.dp)
                    .padding(start = 12.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text =
                stringResource(
                    Res.string.history_intake_amount,
                    intakeHistory.intakeAmount.toCommaSeparated(),
                ),
            style = MulKkamTheme.typography.title2,
            color = Color(intakeHistory.intakeType.toColorHex().toColorInt()),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IntakeHistoryItemPreview() {
    MulKkamTheme {
        IntakeHistoryItem(
            intakeHistory =
                IntakeHistory(
                    id = 1,
                    dateTime = LocalTime(12, 31),
                    intakeAmount = 350,
                    intakeType = IntakeType.WATER,
                    cupEmojiUrl = "url",
                ),
        )
    }
}
