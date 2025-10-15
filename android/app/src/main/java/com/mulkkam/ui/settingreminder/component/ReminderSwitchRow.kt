package com.mulkkam.ui.settingreminder.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.custom.switch.RoundedSwitch
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme

@Composable
fun ReminderSwitchRow(
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 14.dp),
            text = stringResource(R.string.setting_reminder_switch_title),
            style = MulKkamTheme.typography.title2,
            color = Gray400,
        )
        RoundedSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderSwitchRowPreview() {
    MulkkamTheme {
        ReminderSwitchRow(
            checked = true,
            onCheckedChange = {},
        )
    }
}
