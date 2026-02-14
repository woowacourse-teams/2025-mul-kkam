package com.mulkkam.ui.friends.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.domain.model.friend.FriendsResult
import com.mulkkam.ui.component.MulKkamAlertDialog
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.friends.FriendsViewModel
import com.mulkkam.ui.friends.component.FriendItem
import com.mulkkam.ui.friends.component.FriendsEditModeButton
import com.mulkkam.ui.friends.component.FriendsEmptyContent
import com.mulkkam.ui.friends.component.FriendsErrorContent
import com.mulkkam.ui.friends.component.FriendsTopAppBar
import com.mulkkam.ui.friends.model.FriendsDisplayMode
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.util.extensions.OnLoadMore
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.friends_delete_dialog_description
import mulkkam.shared.generated.resources.friends_delete_dialog_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FriendsScreen(
    padding: PaddingValues,
    onNavigateToSearchMembers: () -> Unit,
    onNavigateToPendingFriends: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendsViewModel = koinViewModel(),
) {
    val friendsUiState by viewModel.friendsUiState.collectAsStateWithLifecycle()
    val friendRequestCountUiState by viewModel.friendRequestCountUiState.collectAsStateWithLifecycle()
    val displayMode by viewModel.displayMode.collectAsStateWithLifecycle()
    val hasMoreFriends by viewModel.hasMoreFriends.collectAsStateWithLifecycle()
    val loadMoreUiState by viewModel.loadMoreUiState.collectAsStateWithLifecycle()

    val friendRequestCount: Long = friendRequestCountUiState.toSuccessDataOrNull() ?: 0L

    var friendToDelete: Friend? by rememberSaveable { mutableStateOf(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = White,
        modifier = modifier.fillMaxSize().padding(padding),
        topBar = {
            FriendsTopAppBar(
                onSearchClick = onNavigateToSearchMembers,
                onFriendRequestsClick = onNavigateToPendingFriends,
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
                is MulKkamUiState.Idle -> {
                    Unit
                }

                is MulKkamUiState.Loading -> {
                    Unit
                }

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
                            onThrowWaterBalloon = { friend ->
                                viewModel.throwWaterBalloon(friend)
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

        MulKkamAlertDialog(
            title = stringResource(Res.string.friends_delete_dialog_title, targetFriend.nickname),
            description = stringResource(Res.string.friends_delete_dialog_description),
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
    onThrowWaterBalloon: (friend: Friend) -> Unit,
    onDeleteFriend: (friend: Friend) -> Unit,
) {
    val listState = rememberLazyListState()
    listState.OnLoadMore(isLoadMoreEnabled = hasMore, action = onLoadMore)

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
    MulKkamTheme {
        FriendsScreen(
            padding = PaddingValues(),
            onNavigateToSearchMembers = {},
            onNavigateToPendingFriends = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendItemsPreview() {
    MulKkamTheme {
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
