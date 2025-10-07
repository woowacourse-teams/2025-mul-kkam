package com.mulkkam.ui.settingreminder.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.custom.numberpicker.CustomNumberPicker
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScheduleBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    currentTime: LocalTime = LocalTime.now(),
) {
    var hour by rememberSaveable { mutableIntStateOf(currentTime.hour) }
    var minute by rememberSaveable { mutableIntStateOf(currentTime.minute) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        modifier = Modifier,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.setting_reminder_bottom_sheet_title),
                    color = Gray400,
                    style = MulKkamTheme.typography.title1,
                )
                Icon(
                    painter =
                        painterResource(R.drawable.ic_bio_info_weight_close),
                    contentDescription = stringResource(R.string.setting_reminder_bottom_sheet_close_btn_description),
                    tint = Gray400,
                    modifier = Modifier.clickable { onDismiss() },
                )
            }

            ReminderScheduleBottomSheetTimePicker(
                hour = hour,
                onHourChanged = { hour = it },
                minute = minute,
                onMinuteChanged = { minute = it },
            )

            Text(
                text = stringResource(R.string.setting_reminder_bottom_sheet_complete),
                modifier =
                    Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary200)
                        .padding(horizontal = 24.dp)
                        .clickable { onSelected(LocalTime.of(hour, minute)) }
                        .wrapContentHeight(Alignment.CenterVertically),
                style = MulKkamTheme.typography.title2,
                color = White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ReminderScheduleBottomSheetPreview() {
    MulkkamTheme {
        ReminderScheduleBottomSheet(
            sheetState = rememberStandardBottomSheetState(),
            onDismiss = {},
            onSelected = {},
        )
    }
}
