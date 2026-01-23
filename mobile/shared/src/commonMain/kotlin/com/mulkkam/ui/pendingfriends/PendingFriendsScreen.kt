package com.mulkkam.ui.pendingfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.pendingfriends.component.PendingFriendsTopAppBar
import com.mulkkam.ui.pendingfriends.component.ReceivedTab
import com.mulkkam.ui.pendingfriends.component.SentTab
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.pending_friends_received_request
import mulkkam.shared.generated.resources.pending_friends_sent_request
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PendingFriendsScreen(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    viewModel: PendingFriendsViewModel = koinViewModel(),
) {
    val tabTitles =
        listOf(
            stringResource(Res.string.pending_friends_received_request),
            stringResource(Res.string.pending_friends_sent_request),
        )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

    val receivedRequests by viewModel.receivedRequests.collectAsStateWithLifecycle()
    val sentRequests by viewModel.sentRequests.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            PendingFriendsTopAppBar(onNavigateToBack)
        },
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
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .navigationBarsPadding(),
        ) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                        color = Black,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                tabTitles.forEachIndexed { index: Int, title: String ->
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
            ) { page: Int ->
                when (page) {
                    0 -> {
                        ReceivedTab(
                            receivedRequests =
                                receivedRequests.toSuccessDataOrNull()
                                    ?: emptyList(),
                            onAccept = viewModel::acceptFriend,
                            onReject = viewModel::rejectFriend,
                            onLoadMore = viewModel::loadMoreReceivedFriendsRequest,
                        )
                    }

                    1 -> {
                        SentTab(
                            sentRequests = sentRequests.toSuccessDataOrNull() ?: emptyList(),
                            onCancel = viewModel::cancelRequest,
                            onLoadMore = viewModel::loadMoreSentFriendsRequest,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingFriendsScreenPreview() {
    MulKkamTheme {
        PendingFriendsScreen(
            padding = PaddingValues(),
            onNavigateToBack = { true },
        )
    }
}
