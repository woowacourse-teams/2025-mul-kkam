package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.home_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeTopBar(
    alarmUnreadCount: Long,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 28.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(resource = Res.string.home_label),
            style = MulKkamTheme.typography.headline1,
            color = Black,
            modifier = Modifier.weight(1f),
        )
        NotificationCounter(
            count = alarmUnreadCount,
            onClick = onNotificationClick,
        )
    }
}

@Preview
@Composable
private fun HomeTopBarPreview() {
    MulKkamTheme {
        HomeTopBar(
            alarmUnreadCount = 12,
            onNotificationClick = {},
        )
    }
}
