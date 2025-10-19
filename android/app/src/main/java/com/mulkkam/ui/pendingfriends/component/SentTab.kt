package com.mulkkam.ui.pendingfriends.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.friends.FriendsRequestInfo
import com.mulkkam.ui.designsystem.Gray100
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
                pendingFriend = friendsRequest,
                onCancel = { onCancel(friendsRequest) },
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Gray100,
            )
        }
    }
}
