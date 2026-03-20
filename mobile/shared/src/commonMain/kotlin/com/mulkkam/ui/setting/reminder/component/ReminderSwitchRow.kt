package com.mulkkam.ui.setting.reminder.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.component.RoundedSwitch
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_reminder_switch_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReminderSwitchRow(
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit,
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
            text = stringResource(resource = Res.string.setting_reminder_switch_title),
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
    MulKkamTheme {
        ReminderSwitchRow(
            checked = true,
            onCheckedChange = {},
        )
    }
}
