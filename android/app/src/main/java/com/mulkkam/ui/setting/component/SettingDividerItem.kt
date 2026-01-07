package com.mulkkam.ui.setting.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray50

@Composable
fun SettingDividerItem() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 8.dp,
        color = Gray50,
    )
}
