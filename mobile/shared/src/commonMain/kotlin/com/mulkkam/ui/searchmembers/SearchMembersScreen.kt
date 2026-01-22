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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.component.MulKkamInfoDialog
import com.mulkkam.ui.component.MulKkamTextField
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
import kotlinx.coroutines.FlowPreview
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_search_friends_search
import mulkkam.shared.generated.resources.search_friends_accept_request_confirmed
import mulkkam.shared.generated.resources.search_friends_accept_request_warning
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchMembersScreen(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    receivedMemberSearchInfo: MemberSearchInfo?,
    showDialog: Boolean,
    onDismissDialog: () -> Unit,
    onConfirmDialog: (MemberSearchInfo) -> Unit,
    state: LazyListState = rememberLazyListState(),
    viewModel: SearchMembersViewModel = koinViewModel(),
) {
    val memberSearchUiState by viewModel.memberSearchUiState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()

    val loadMoreState by viewModel.loadUiState.collectAsStateWithLifecycle()
    state.OnLoadMore(action = viewModel::loadMoreMembers)

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
                    onConfirmDialog(memberSearchInfo)
                },
                onDismiss = onDismissDialog,
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

@Preview(showBackground = true)
@Composable
private fun SearchMembersScreenPreview() {
    MulKkamTheme {
        SearchMembersScreen(
            padding = PaddingValues(),
            onNavigateToBack = { true },
            receivedMemberSearchInfo = null,
            showDialog = false,
            onDismissDialog = {},
            onConfirmDialog = {},
        )
    }
}
