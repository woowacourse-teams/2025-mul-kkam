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
import androidx.compose.ui.tooling.preview.Preview
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.MulkkamTheme
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
    var showDialog by remember { mutableStateOf(false) }
    var requestToCancel: FriendsRequestInfo? by remember { mutableStateOf(null) }

    LazyColumn(modifier = modifier.fillMaxHeight(), state = state) {
        items(
            sentRequests.size,
            key = { sentRequests[it].memberId },
        ) { index ->
            val friendsRequest = sentRequests[index]
            SentRequestItem(
                modifier = Modifier.animateItem(),
                sentRequest = friendsRequest,
                onCancel = {
                    requestToCancel = friendsRequest
                    showDialog = true
                },
            )
        }
    }

    if (showDialog) {
        CancelFriendsRequestDialog(
            friendsRequest = requestToCancel ?: return,
            onConfirm = {
                onCancel(requestToCancel ?: return@CancelFriendsRequestDialog)
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SentTabPreview() {
    MulkkamTheme {
        SentTab(
            sentRequests =
                listOf(
                    FriendsRequestInfo(
                        memberId = 1L,
                        nickname = Nickname("돈가스먹는환노"),
                    ),
                    FriendsRequestInfo(
                        memberId = 2L,
                        nickname = Nickname("돈가스먹는공백"),
                    ),
                    FriendsRequestInfo(
                        memberId = 3L,
                        nickname = Nickname("돈가스안먹는이든"),
                    ),
                ),
            onCancel = {},
            onLoadMore = {},
        )
    }
}
