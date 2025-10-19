package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.util.extensions.onLoadMore

@Composable
fun ReceivedTab(
    pendingFriends: List<FriendsRequestInfo>,
    onAccept: (FriendsRequestInfo) -> Unit,
    onReject: (FriendsRequestInfo) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    state.onLoadMore(action = onLoadMore)

    LazyColumn(modifier = modifier.fillMaxHeight(), state = state) {
        items(
            pendingFriends.size,
            key = { pendingFriends[it].requestId },
        ) { index ->
            val friendsRequestInfo = pendingFriends[index]
            ReceivedRequestItem(
                pendingFriend = pendingFriends[index],
                onAccept = { onAccept(friendsRequestInfo) },
                onReject = { onReject(friendsRequestInfo) },
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Gray100,
            )
        }
    }
}
