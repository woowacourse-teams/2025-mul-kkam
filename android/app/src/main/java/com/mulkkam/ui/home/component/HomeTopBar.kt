package com.mulkkam.ui.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme

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
            text = stringResource(R.string.home_label),
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

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    MulkkamTheme {
        HomeTopBar(
            alarmUnreadCount = 12,
            onNotificationClick = {},
        )
    }
}
