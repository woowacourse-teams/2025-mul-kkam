package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showDialog by remember { mutableStateOf(false) }
    var requestToReject: FriendsRequestInfo? by remember { mutableStateOf(null) }

    LazyColumn(modifier = modifier.fillMaxHeight(), state = state) {
        items(
            receivedRequests.size,
            key = { receivedRequests[it].memberId },
        ) { index ->
            val friendsRequest = receivedRequests[index]
            ReceivedRequestItem(
                receivedRequest = receivedRequests[index],
                onAccept = { onAccept(friendsRequest) },
                onReject = {
                    requestToReject = friendsRequest
                    showDialog = true
                },
                modifier = Modifier.animateItem(),
            )
        }
    }

    if (showDialog) {
        RejectFriendsRequestDialog(
            friendsRequest = requestToReject ?: return,
            onConfirm = {
                onReject(requestToReject ?: return@RejectFriendsRequestDialog)
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}
