package com.mulkkam.ui.setting.setting.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.component.RoundedSwitch
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme

@Composable
fun SettingSwitchItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MulKkamTheme.typography.body2,
            color = Black,
            modifier = Modifier.weight(1f),
        )
        RoundedSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}
