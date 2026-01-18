package com.mulkkam.ui.searchmembers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.component.MulKkamInfoDialog
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.MulKkamTextField
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.LoadMoreButton
import com.mulkkam.ui.searchmembers.component.SearchMembersItem
import com.mulkkam.ui.searchmembers.component.SearchMembersTopAppBar
import com.mulkkam.ui.util.extensions.OnLoadMore
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_search_friends_search
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.search_friends_accept_request_confirmed
import mulkkam.shared.generated.resources.search_friends_accept_request_warning
import mulkkam.shared.generated.resources.search_friends_accept_success
import mulkkam.shared.generated.resources.search_friends_request_failed
import mulkkam.shared.generated.resources.search_friends_request_success
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchMembersScreen(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit = {},
    state: LazyListState = rememberLazyListState(),
    viewModel: SearchMembersViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val memberSearchUiState by viewModel.memberSearchUiState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()

    val loadMoreState by viewModel.loadUiState.collectAsStateWithLifecycle()
    state.OnLoadMore(action = viewModel::loadMoreMembers)

    var receivedMemberSearchInfo: MemberSearchInfo? by remember { mutableStateOf(null) }
    var showDialog: Boolean by remember { mutableStateOf(false) }

    viewModel.onRequestFriends.collectWithLifecycle(lifecycleOwner) { uiState ->
        handleRequestFriendsAction(
            state = uiState,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
        )
    }

    viewModel.onAcceptFriends.collectWithLifecycle(lifecycleOwner) { uiState ->
        handleAcceptFriendsAction(
            state = uiState,
            snackbarHostState = snackbarHostState,
            onFriendAccepted = onFriendAccepted,
            coroutineScope = coroutineScope,
        )
    }

    viewModel.receivedMemberSearchInfo.collectWithLifecycle(lifecycleOwner) { uiState ->
        receivedMemberSearchInfo = uiState.toSuccessDataOrNull()
        showDialog = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { SearchMembersTopAppBar { onNavigateToBack() } },
        containerColor = White,
        modifier =
            Modifier.background(White).padding(
                PaddingValues(
                    start = padding.calculateLeftPadding(LayoutDirection.Ltr),
                    top = 0.dp,
                    end = padding.calculateRightPadding(LayoutDirection.Ltr),
                    bottom = padding.calculateBottomPadding(),
                ),
            ),
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        if (showDialog) {
            val memberSearchInfo: MemberSearchInfo = receivedMemberSearchInfo ?: return@Scaffold
            MulKkamInfoDialog(
                title =
                    stringResource(
                        Res.string.search_friends_accept_request_confirmed,
                        memberSearchInfo.nickname.name,
                    ),
                description = stringResource(Res.string.search_friends_accept_request_warning),
                onConfirm = {
                    viewModel.acceptFriendRequest(memberSearchInfo)
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }

        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            MulKkamTextField(
                value = name,
                onValueChanged = viewModel::updateName,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(44.dp),
                prefix = { prefixModifier ->
                    Icon(
                        modifier = prefixModifier,
                        painter = painterResource(Res.drawable.ic_search_friends_search),
                        contentDescription = null,
                        tint = Gray300,
                    )
                },
                maxLength = Nickname.NICKNAME_LENGTH_MAX,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            )

            if (!isTyping && name.isNotEmpty() && memberSearchUiState.toSuccessDataOrNull()?.isEmpty() == true) {
                EmptySearchMembersScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            LazyColumn(
                state = state,
            ) {
                val searchMembers: List<MemberSearchInfo> =
                    memberSearchUiState.toSuccessDataOrNull() ?: return@LazyColumn

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

private fun handleRequestFriendsAction(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.search_friends_request_success),
                    iconResource = Res.drawable.ic_terms_all_check_on,
                )
            }
        }

        is MulKkamUiState.Failure -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.search_friends_request_failed),
                    iconResource = Res.drawable.ic_alert_circle,
                )
            }
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}

private fun handleAcceptFriendsAction(
    state: MulKkamUiState<String>,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit,
    coroutineScope: CoroutineScope,
) {
    when (state) {
        is MulKkamUiState.Success<String> -> {
            val nickname: String = state.toSuccessDataOrNull() ?: return
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.search_friends_accept_success, nickname),
                    iconResource = Res.drawable.ic_terms_all_check_on,
                )
            }
            onFriendAccepted()
        }

        is MulKkamUiState.Failure -> {
            Unit
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchMembersScreenPreview() {
    MulKkamTheme {
        SearchMembersScreen(
            padding = PaddingValues(),
            onNavigateToBack = { true },
            snackbarHostState = SnackbarHostState(),
        )
    }
}
