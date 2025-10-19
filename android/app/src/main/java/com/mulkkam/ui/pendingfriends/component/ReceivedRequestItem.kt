package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import java.time.Duration
import java.time.LocalDateTime

private const val HOURS_PER_DAY: Int = 24

@Composable
fun ReceivedRequestItem(
    pendingFriend: FriendsRequestInfo,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
    currentTime: LocalDateTime = LocalDateTime.now(),
) {
    Column(
        modifier = modifier,
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
                    text = pendingFriend.nickname.name,
                    color = Black,
                    style = MulKkamTheme.typography.title1,
                )
                Text(
                    text = formatRemainingTime(currentTime, LocalDateTime.now()),
                    color = Gray300,
                    style = MulKkamTheme.typography.label2,
                )
            }
            Row {
                AcceptFriendButton(onAccept)
                RejectFriendButton(onReject)
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Gray100,
        )
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
                FriendsRequestInfo(
                    requestId = 1L,
                    nickname = Nickname("돈가스먹는환노"),
                ),
            currentTime = LocalDateTime.of(2025, 10, 13, 18, 0),
            onAccept = {},
            onReject = {},
        )
    }
}
