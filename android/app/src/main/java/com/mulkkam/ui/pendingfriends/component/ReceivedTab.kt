package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.friends.PendingFriend
import com.mulkkam.ui.designsystem.Gray100

@Composable
fun ReceivedTab(
    pendingFriends: List<PendingFriend>,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        items(
            pendingFriends.size,
            key = { pendingFriends[it].name },
        ) { index ->
            ReceivedRequestItem(
                pendingFriend = pendingFriends[index],
                onAccept = { onAccept },
                onReject = { onReject },
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Gray100,
            )
        }
    }
}
