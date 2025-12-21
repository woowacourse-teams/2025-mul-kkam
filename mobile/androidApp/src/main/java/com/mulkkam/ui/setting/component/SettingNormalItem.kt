package com.mulkkam.ui.setting.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme

@Composable
fun SettingNormalItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MulKkamTheme.typography.body2,
            color = Black,
        )
        Icon(
            painter = painterResource(R.drawable.ic_common_next),
            contentDescription = stringResource(R.string.notification_app_bar_navigation_icon_description),
            tint = Gray400,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingNormalItemPreview() {
    MulKkamTheme {
        SettingNormalItem(label = "서비스 운영 정책", onClick = {})
    }
}
