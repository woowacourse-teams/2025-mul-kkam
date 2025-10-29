package com.mulkkam.ui.searchmembers

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.dialog.MulKkamInfoDialog
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.LoadMoreButton
import com.mulkkam.ui.searchmembers.component.SearchMembersItem
import com.mulkkam.ui.searchmembers.component.SearchMembersTextField
import com.mulkkam.ui.searchmembers.component.SearchMembersTopAppBar
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import com.mulkkam.ui.util.extensions.onLoadMore
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchMembersScreen(
    navigateToBack: () -> Unit,
    onFriendAccepted: () -> Unit,
    state: LazyListState = rememberLazyListState(),
    viewModel: SearchMembersViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val memberSearchUiState by viewModel.memberSearchUiState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()

    val loadMoreState by viewModel.loadUiState.collectAsStateWithLifecycle()
    state.onLoadMore(action = { viewModel.loadMoreMembers() })

    var receivedMemberSearchInfo: MemberSearchInfo? by remember { mutableStateOf(null) }
    var showDialog by remember { mutableStateOf(false) }

    viewModel.onRequestFriends.collectWithLifecycle(lifecycleOwner) { state ->
        handleRequestFriendsAction(
            state = state,
            snackbarHostState = snackbarHostState,
            context = context,
        )
    }

    viewModel.onAcceptFriends.collectWithLifecycle(lifecycleOwner) { state ->
        handleAcceptFriendsAction(
            state = state,
            snackbarHostState = snackbarHostState,
            context = context,
            onFriendAccepted = onFriendAccepted,
        )
    }

    viewModel.receivedMemberSearchInfo.collectWithLifecycle(lifecycleOwner) { state ->
        receivedMemberSearchInfo = state.toSuccessDataOrNull()
        showDialog = true
    }

    Scaffold(
        topBar = { SearchMembersTopAppBar(navigateToBack) },
        containerColor = White,
        modifier = Modifier.systemBarsPadding(),
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        if (showDialog) {
            val memberSearchInfo = receivedMemberSearchInfo ?: return@Scaffold
            MulKkamInfoDialog(
                title =
                    stringResource(
                        R.string.search_friends_accept_request_confirmed,
                        memberSearchInfo.nickname.name,
                    ),
                description = stringResource(R.string.search_friends_accept_request_warning),
                onConfirm = {
                    viewModel.acceptFriendRequest(
                        memberSearchInfo,
                    )
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }

        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            SearchMembersTextField(name) { viewModel.updateName(it) }

            if (!isTyping && name.isNotEmpty() && memberSearchUiState.toSuccessDataOrNull()?.size == 0) {
                EmptySearchMembersScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            LazyColumn(
                state = state,
            ) {
                val searchMembers = memberSearchUiState.toSuccessDataOrNull() ?: return@LazyColumn

                items(
                    count = searchMembers.size,
                    key = { index -> searchMembers[index].nickname.name },
                ) { index ->

                    SearchMembersItem(
                        memberSearchInfo = searchMembers[index],
                        onClick = { viewModel.requestFriends(searchMembers[index]) },
                    )

                    if (index == searchMembers.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Gray100,
                        )
                    }
                }

                if (loadMoreState is MulKkamUiState.Failure) {
                    item {
                        LoadMoreButton { viewModel.loadMoreMembers() }
                    }
                }
            }
        }
    }
}

private suspend fun handleRequestFriendsAction(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
    context: Context,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(context, R.string.search_friends_request_success),
                iconResourceId = R.drawable.ic_terms_all_check_on,
            )
        }

        is MulKkamUiState.Failure -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(context, R.string.search_friends_request_failed),
                iconResourceId = R.drawable.ic_alert_circle,
            )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}

private suspend fun handleAcceptFriendsAction(
    state: MulKkamUiState<String>,
    snackbarHostState: SnackbarHostState,
    context: Context,
    onFriendAccepted: () -> Unit,
) {
    when (state) {
        is MulKkamUiState.Success<String> -> {
            snackbarHostState.showMulKkamSnackbar(
                message =
                    context.getString(
                        R.string.search_friends_accept_success,
                        state.toSuccessDataOrNull(),
                    ),
                iconResourceId = R.drawable.ic_terms_all_check_on,
            )
            onFriendAccepted()
        }

        is MulKkamUiState.Failure -> Unit
        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchMembersScreenPreview() {
    MulkkamTheme {
        SearchMembersScreen(
            navigateToBack = {},
            onFriendAccepted = {},
        )
    }
}
