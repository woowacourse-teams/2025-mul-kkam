package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.ui.util.extensions.onLoadMore

@Composable
fun SentTab(
    sentRequests: List<FriendsRequestInfo>,
    onCancel: (FriendsRequestInfo) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    state.onLoadMore(action = onLoadMore)

    LazyColumn(modifier = modifier.fillMaxHeight(), state = state) {
        items(
            sentRequests.size,
            key = { sentRequests[it].requestId },
        ) { index ->
            val friendsRequest = sentRequests[index]
            SentRequestItem(
                modifier = Modifier.animateItem(),
                pendingFriend = friendsRequest,
                onCancel = { onCancel(friendsRequest) },
            )
        }
    }
}
