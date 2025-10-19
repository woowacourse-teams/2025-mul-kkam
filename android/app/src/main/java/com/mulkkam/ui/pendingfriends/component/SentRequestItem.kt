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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulKkamTheme

@Composable
fun SentRequestItem(
    pendingFriend: FriendsRequestInfo,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
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
            }
            Row {
                RejectFriendButton(onCancel)
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Gray100,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SentRequestItemPreview() {
    SentRequestItem(
        FriendsRequestInfo(
            requestId = 1L,
            nickname = Nickname("돈가스먹는환노"),
        ),
        onCancel = {},
    )
}
