package com.mulkkam.ui.settingreminder.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.custom.numberpicker.CustomNumberPicker
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme

@Composable
fun ReminderScheduleBottomSheetTimePicker(
    hour: Int,
    onHourChanged: (Int) -> Unit,
    minute: Int,
    onMinuteChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 18.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomNumberPicker(
            range = IntRange(0, 23),
            value = hour,
            onValueChange = { onHourChanged(it) },
            modifier =
                Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
        )
        Text(
            text = ":",
            color = Gray400,
            style = MulKkamTheme.typography.title1,
        )
        CustomNumberPicker(
            range = IntRange(0, 59),
            value = minute,
            onValueChange = { onMinuteChanged(it) },
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
        )
    }
}
