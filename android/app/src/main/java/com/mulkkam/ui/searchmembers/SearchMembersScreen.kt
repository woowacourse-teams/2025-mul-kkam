package com.mulkkam.ui.searchmembers

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.LoadMoreButton
import com.mulkkam.ui.searchmembers.component.SearchMembersItem
import com.mulkkam.ui.searchmembers.component.SearchMembersTextField
import com.mulkkam.ui.searchmembers.component.SearchMembersTopAppBar
import com.mulkkam.ui.util.extensions.onLoadMore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchMembersScreen(
    navigateToBack: () -> Unit,
    state: LazyListState = rememberLazyListState(),
    viewModel: SearchMembersViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val view = LocalView.current

    val memberSearchUiState by viewModel.memberSearchUiState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()

    val loadMoreState by viewModel.loadUiState.collectAsStateWithLifecycle()
    state.onLoadMore(action = { viewModel.loadMoreMembers() })

    LaunchedEffect(Unit) {
        launch {
            viewModel.onRequestFriends.collect { state ->
                handleRequestFriendsAction(state, view, context)
            }
        }

        launch {
            viewModel.onAcceptFriends.collect { state ->
                handleAcceptFriendsAction(state, view, context)
            }
        }
    }

    Scaffold(
        topBar = { SearchMembersTopAppBar(navigateToBack) },
        containerColor = White,
    ) { innerPadding ->
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

private fun handleRequestFriendsAction(
    state: MulKkamUiState<Unit>,
    view: View,
    context: Context,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            CustomSnackBar
                .make(
                    view,
                    getString(context, R.string.search_friends_request_success),
                    R.drawable.ic_terms_all_check_on,
                ).show()
        }

        is MulKkamUiState.Failure -> {
            CustomSnackBar
                .make(
                    view,
                    getString(context, R.string.search_friends_request_failed),
                    R.drawable.ic_alert_circle,
                ).show()
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}

private fun handleAcceptFriendsAction(
    state: MulKkamUiState<String>,
    view: View,
    context: Context,
) {
    when (state) {
        is MulKkamUiState.Success<String> -> {
            CustomSnackBar
                .make(
                    view,
                    context.getString(R.string.search_friends_accept_success, state.toSuccessDataOrNull()),
                    R.drawable.ic_terms_all_check_on,
                ).show()
        }

        is MulKkamUiState.Failure -> Unit
        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchMembersScreenPreview() {
    MulkkamTheme {
        SearchMembersScreen(navigateToBack = {})
    }
}
