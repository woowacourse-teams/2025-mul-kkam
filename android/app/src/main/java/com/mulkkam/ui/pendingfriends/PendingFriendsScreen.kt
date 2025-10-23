package com.mulkkam.ui.pendingfriends

import android.content.Context
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.pendingfriends.component.PendingFriendsTopAppBar
import com.mulkkam.ui.pendingfriends.component.ReceivedTab
import com.mulkkam.ui.pendingfriends.component.SentTab
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun PendingFriendsScreen(
    navigateToBack: () -> Unit,
    onFriendAccepted: () -> Unit,
    viewModel: PendingFriendsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val view = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val tabTitles =
        listOf(
            stringResource(R.string.pending_friends_received_request),
            stringResource(R.string.pending_friends_sent_request),
        )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

    val receivedRequests by viewModel.receivedRequests.collectAsStateWithLifecycle()
    val sentRequests by viewModel.sentRequests.collectAsStateWithLifecycle()

    viewModel.onAcceptRequest.collectWithLifecycle(lifecycleOwner) { state ->
        handleAcceptRequestAction(
            state = state,
            view = view,
            context = context,
            onFriendAccepted = onFriendAccepted,
        )
    }

    viewModel.onRejectRequest.collectWithLifecycle(lifecycleOwner) { state ->
        handleRejectRequestAction(state, view, context)
    }

    viewModel.onCancelRequest.collectWithLifecycle(lifecycleOwner) { state ->
        handleRejectRequestAction(state, view, context)
    }

    Scaffold(
        topBar = {
            PendingFriendsTopAppBar(navigateToBack)
        },
        containerColor = White,
        modifier = Modifier.systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Black,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                style = MulKkamTheme.typography.title2,
                                color = Black,
                            )
                        },
                        modifier = Modifier.background(White),
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 ->
                        ReceivedTab(
                            receivedRequests =
                                receivedRequests.toSuccessDataOrNull()
                                    ?: emptyList(),
                            onAccept = { viewModel.acceptFriend(it) },
                            onReject = { viewModel.rejectFriend(it) },
                            onLoadMore = { viewModel.loadMoreReceivedFriendsRequest() },
                        )

                    1 ->
                        SentTab(
                            sentRequests = sentRequests.toSuccessDataOrNull() ?: emptyList(),
                            onCancel = { viewModel.cancelRequest(it) },
                            onLoadMore = { viewModel.loadMoreSentFriendsRequest() },
                        )
                }
            }
        }
    }
}

private fun handleAcceptRequestAction(
    state: MulKkamUiState<String>,
    view: View,
    context: Context,
    onFriendAccepted: () -> Unit,
) {
    when (state) {
        is MulKkamUiState.Success<String> -> {
            val nickname = state.toSuccessDataOrNull() ?: return
            CustomSnackBar
                .make(
                    view,
                    context.getString(R.string.pending_friends_accept_success, nickname),
                    R.drawable.ic_terms_all_check_on,
                ).show()
            onFriendAccepted()
        }

        is MulKkamUiState.Failure -> {
            CustomSnackBar
                .make(
                    view,
                    getString(context, R.string.pending_friends_accept_failed),
                    R.drawable.ic_info_circle,
                ).show()
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}

private fun handleRejectRequestAction(
    state: MulKkamUiState<Unit>,
    view: View,
    context: Context,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            CustomSnackBar
                .make(
                    view,
                    context.getString(R.string.pending_friends_reject_success),
                    R.drawable.ic_terms_all_check_on,
                ).show()
        }

        is MulKkamUiState.Failure -> Unit
        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}

private fun handleCancelRequestAction(
    state: MulKkamUiState<Unit>,
    view: View,
    context: Context,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            CustomSnackBar
                .make(
                    view,
                    context.getString(R.string.pending_friends_cancel_success),
                    R.drawable.ic_terms_all_check_on,
                ).show()
        }

        is MulKkamUiState.Failure -> Unit
        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingFriendsScreenPreview() {
    MulkkamTheme {
        PendingFriendsScreen(
            navigateToBack = {},
            onFriendAccepted = {},
        )
    }
}
