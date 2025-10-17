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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.friends.PendingFriend
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import java.time.LocalDateTime

@Composable
fun SentRequestItem(
    pendingFriend: PendingFriend,
    onCancel: () -> Unit,
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
        }
        Row {
            RejectFriendButton(onCancel)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SentRequestItemPreview() {
    SentRequestItem(
        PendingFriend(name = "hwannow", time = LocalDateTime.of(2025, 10, 14, 10, 0)),
        onCancel = {},
    )
}
