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
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.intake.IntakeHistory
import com.mulkkam.ui.component.NetworkImage
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.ImageShape
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.history_intake_amount
import mulkkam.shared.generated.resources.img_cup_placeholder
import org.jetbrains.compose.resources.stringResource

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
            placeholderRes = Res.drawable.img_cup_placeholder,
            shape = ImageShape.Circle,
        )

        Text(
            text = formatTime(intakeHistory.dateTime),
            style = MulKkamTheme.typography.body4,
            color = Black,
            modifier =
                Modifier
                    .padding(vertical = 14.dp)
                    .padding(start = 12.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(Res.string.history_intake_amount, intakeHistory.intakeAmount),
            style = MulKkamTheme.typography.title2,
            color =
                Color(
                    intakeHistory.intakeType
                        .toColorHex()
                        .toLongOrNull(16)
                        ?.toInt() ?: 0xFF000000.toInt(),
                ),
        )
    }
}

private fun formatTime(time: kotlinx.datetime.LocalTime): String {
    val hour = time.hour
    val minute = time.minute
    val period = if (hour < 12) "오전" else "오후"
    val displayHour =
        if (hour == 0) {
            12
        } else if (hour > 12) {
            hour - 12
        } else {
            hour
        }

    return if (minute == 0) {
        "$period ${displayHour}시"
    } else {
        "$period ${displayHour}시 ${minute}분"
    }
}
