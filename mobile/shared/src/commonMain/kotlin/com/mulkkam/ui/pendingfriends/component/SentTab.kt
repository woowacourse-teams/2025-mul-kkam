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
import com.mulkkam.ui.component.MulKkamAlertDialog
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.extensions.OnLoadMore
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.pending_friends_cancel_request_confirmed
import mulkkam.shared.generated.resources.pending_friends_cancel_request_warning
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SentTab(
    sentRequests: List<FriendsRequestInfo>,
    onCancel: (FriendsRequestInfo) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    state.OnLoadMore(action = onLoadMore)
    var showDialog: Boolean by remember { mutableStateOf(false) }
    var requestToCancel: FriendsRequestInfo? by remember { mutableStateOf(null) }

    LazyColumn(modifier = modifier.fillMaxHeight(), state = state) {
        items(
            sentRequests.size,
            key = { sentRequests[it].memberId },
        ) { index: Int ->
            val friendsRequest: FriendsRequestInfo = sentRequests[index]
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
        MulKkamAlertDialog(
            title =
                stringResource(
                    Res.string.pending_friends_cancel_request_confirmed,
                    requestToCancel?.nickname?.name ?: return,
                ),
            description = stringResource(Res.string.pending_friends_cancel_request_warning),
            onConfirm = {
                onCancel(requestToCancel ?: return@MulKkamAlertDialog)
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SentTabPreview() {
    MulKkamTheme {
        SentTab(
            sentRequests = emptyList(),
            onCancel = {},
            onLoadMore = {},
        )
    }
}
