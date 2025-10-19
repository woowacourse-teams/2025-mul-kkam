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
fun ReceivedTab(
    receivedRequests: List<FriendsRequestInfo>,
    onAccept: (FriendsRequestInfo) -> Unit,
    onReject: (FriendsRequestInfo) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    state.onLoadMore(action = onLoadMore)

    LazyColumn(modifier = modifier.fillMaxHeight(), state = state) {
        items(
            receivedRequests.size,
            key = { receivedRequests[it].requestId },
        ) { index ->
            val friendsRequestInfo = receivedRequests[index]
            ReceivedRequestItem(
                receivedRequest = receivedRequests[index],
                onAccept = { onAccept(friendsRequestInfo) },
                onReject = { onReject(friendsRequestInfo) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}
