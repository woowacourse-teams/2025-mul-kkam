package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.mulkkam.domain.model.friends.PendingFriend
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import java.time.Duration
import java.time.LocalDateTime

private const val HOURS_PER_DAY: Int = 24

@Composable
fun ReceivedRequestItem(
    pendingFriend: PendingFriend,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    currentTime: LocalDateTime = LocalDateTime.now(),
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = pendingFriend.name,
                color = Black,
                style = MulKkamTheme.typography.title1,
            )
            Text(
                text = formatRemainingTime(currentTime, pendingFriend.time),
                color = Gray300,
                style = MulKkamTheme.typography.label2,
            )
        }
        Row {
            AcceptFriendButton(onAccept)
            RejectFriendButton(onReject)
        }
    }
}

@Composable
private fun formatRemainingTime(
    currentTime: LocalDateTime,
    requestTime: LocalDateTime,
): String {
    val duration = Duration.between(requestTime, currentTime)
    val hours = duration.toHours()

    return if (hours < HOURS_PER_DAY) {
        stringResource(R.string.pending_friends_hours_ago, hours)
    } else {
        val days = hours / HOURS_PER_DAY
        stringResource(R.string.pending_friends_days_ago, days)
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceivedRequestItemPreview() {
    MulkkamTheme {
        ReceivedRequestItem(
            pendingFriend =
                PendingFriend(
                    name = "hwannow",
                    time = LocalDateTime.of(2025, 10, 13, 15, 0),
                ),
            currentTime = LocalDateTime.of(2025, 10, 13, 18, 0),
            onAccept = {},
            onReject = {},
        )
    }
}
