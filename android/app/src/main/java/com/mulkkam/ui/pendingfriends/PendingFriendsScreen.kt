package com.mulkkam.ui.pendingfriends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.pendingfriends.component.PendingFriendsTopAppBar
import com.mulkkam.ui.pendingfriends.component.ReceivedTab
import com.mulkkam.ui.pendingfriends.component.SentTab
import kotlinx.coroutines.launch

@Composable
fun PendingFriendsScreen(
    navigateToBack: () -> Unit,
    viewModel: PendingFriendsViewModel = viewModel(),
) {
    val tabTitles =
        listOf(
            stringResource(R.string.pending_friends_received_request),
            stringResource(R.string.pending_friends_sent_request),
        )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

    val receivedRequests by viewModel.receivedRequest.collectAsStateWithLifecycle()
    val sentRequests by viewModel.sentRequest.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            PendingFriendsTopAppBar(navigateToBack)
        },
        containerColor = White,
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
                            pendingFriends = receivedRequests,
                            onAccept = { viewModel.acceptFriend() },
                            onReject = { viewModel.rejectFriend() },
                        )

                    1 ->
                        SentTab(
                            sentRequests = sentRequests,
                            onCancel = { viewModel.cancelRequest() },
                        )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingFriendsScreenPreview() {
    MulkkamTheme {
        PendingFriendsScreen(navigateToBack = {})
    }
}
