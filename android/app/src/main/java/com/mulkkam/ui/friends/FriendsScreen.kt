package com.mulkkam.ui.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.domain.model.friend.FriendsResult
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.friends.component.FriendDeleteConfirmationDialog
import com.mulkkam.ui.friends.component.FriendItem
import com.mulkkam.ui.friends.component.FriendsEditModeButton
import com.mulkkam.ui.friends.component.FriendsEmptyContent
import com.mulkkam.ui.friends.component.FriendsErrorContent
import com.mulkkam.ui.friends.component.FriendsTopAppBar
import com.mulkkam.ui.friends.model.FriendsDisplayMode
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.util.extensions.onLoadMore

@Composable
fun FriendsScreen(
    navigateToSearch: () -> Unit,
    navigateToFriendRequests: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendsViewModel = hiltViewModel(),
) {
    val friendsUiState by viewModel.friendsUiState.collectAsStateWithLifecycle()
    val friendRequestCountUiState by viewModel.friendRequestCountUiState.collectAsStateWithLifecycle()
    val displayMode by viewModel.displayMode.collectAsStateWithLifecycle()
    val hasMoreFriends by viewModel.hasMoreFriends.collectAsStateWithLifecycle()
    val loadMoreUiState by viewModel.loadMoreUiState.collectAsStateWithLifecycle()

    val friendRequestCount: Long = friendRequestCountUiState.toSuccessDataOrNull() ?: 0L

    var friendToDelete: Friend? by rememberSaveable { mutableStateOf(null) }

    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            FriendsTopAppBar(
                onSearchClick = navigateToSearch,
                onFriendRequestsClick = navigateToFriendRequests,
                friendRequestCount = friendRequestCount,
                modifier = Modifier.padding(top = 20.dp),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
        ) {
            when (friendsUiState) {
                is MulKkamUiState.Idle -> Unit
                is MulKkamUiState.Loading -> Unit

                is MulKkamUiState.Failure -> {
                    FriendsErrorContent(
                        onRetry = { viewModel.loadFriends() },
                    )
                }

                is MulKkamUiState.Success -> {
                    val friendsResult: FriendsResult = friendsUiState.toSuccessDataOrNull() ?: return@Column
                    if (friendsResult.friends.isEmpty()) {
                        FriendsEmptyContent()
                    } else {
                        FriendsEditModeButton(
                            displayMode = displayMode,
                            onClick = viewModel::toggleDisplayMode,
                            modifier =
                                Modifier
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                                    .align(Alignment.End),
                        )
                        HorizontalDivider(color = Gray100, thickness = 1.dp)
                        FriendItems(
                            friends = friendsResult.friends,
                            displayMode = displayMode,
                            hasMore = hasMoreFriends,
                            onLoadMore = viewModel::loadMore,
                            onThrowWaterBalloon = {
                                // TODO: 물풍선 던지기 기능 구현
                            },
                            onDeleteFriend = { friendToDelete = it },
                        )
                    }
                }
            }

            if (loadMoreUiState == MulKkamUiState.Loading) {
                CircularProgressIndicator()
            }
        }
    }

    if (friendToDelete != null) {
        val targetFriend = friendToDelete ?: return

        FriendDeleteConfirmationDialog(
            friend = targetFriend,
            onConfirm = {
                viewModel.deleteFriend(targetFriend.id)
                friendToDelete = null
            },
            onDismiss = { friendToDelete = null },
        )
    }
}

@Composable
private fun FriendItems(
    friends: List<Friend>,
    displayMode: FriendsDisplayMode,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onThrowWaterBalloon: (Friend) -> Unit,
    onDeleteFriend: (Friend) -> Unit,
) {
    val listState = rememberLazyListState()
    listState.onLoadMore(isLoadMoreEnabled = hasMore, action = onLoadMore)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            items = friends,
            key = { friend -> friend.id },
        ) { friend ->
            FriendItem(
                friend = friend,
                displayMode = displayMode,
                onThrowWaterBalloon = { onThrowWaterBalloon(friend) },
                onDeleteFriend = { onDeleteFriend(friend) },
            )
            HorizontalDivider(color = Gray100, thickness = 1.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendsScreenPreview() {
    MulkkamTheme {
        FriendsScreen(
            navigateToSearch = {},
            navigateToFriendRequests = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendItemsPreview() {
    MulkkamTheme {
        FriendItems(
            friends =
                listOf(
                    Friend(id = 1L, nickname = "공백"),
                    Friend(id = 2L, nickname = "환노"),
                    Friend(id = 3L, nickname = "이든"),
                ),
            displayMode = FriendsDisplayMode.VIEWING,
            hasMore = true,
            onLoadMore = {},
            onThrowWaterBalloon = {},
            onDeleteFriend = {},
        )
    }
}
