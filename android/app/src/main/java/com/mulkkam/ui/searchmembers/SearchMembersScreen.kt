package com.mulkkam.ui.searchmembers

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.notification.component.LoadMoreButton
import com.mulkkam.ui.searchmembers.component.SearchMembersItem
import com.mulkkam.ui.searchmembers.component.SearchMembersTextField
import com.mulkkam.ui.searchmembers.component.SearchMembersTopAppBar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchMembersScreen(
    navigateToBack: () -> Unit,
    state: LazyListState = rememberLazyListState(),
    viewModel: SearchMembersViewModel = viewModel(),
) {
    val searchMembersUiState by viewModel.searchMembersUiState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val loadMoreState by viewModel.loadUiState.collectAsStateWithLifecycle()
    state.onLoadMore(action = { viewModel.loadMoreMembers() })

    LaunchedEffect(Unit) {
        viewModel.name.debounce(300L).collect { name ->
            viewModel.searchMembers()
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

            LazyColumn(
                state = state,
            ) {
                val searchMembers = searchMembersUiState.toSuccessDataOrNull() ?: return@LazyColumn

                items(
                    count = searchMembers.size,
                    key = { index -> searchMembers[index].nickname.name },
                ) { index ->

                    SearchMembersItem(
                        name = searchMembers[index].nickname.name,
                        onRequest = { viewModel.requestFriends(searchMembers[index].id) },
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

private fun LazyListState.reachedBottom(preloadThreshold: Int): Boolean {
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 &&
        (lastVisibleItem?.index ?: 0) >= layoutInfo.totalItemsCount - (preloadThreshold + 1)
}

@SuppressLint("ComposableNaming")
@Composable
private fun LazyListState.onLoadMore(
    preloadThreshold: Int = 6,
    action: () -> Unit,
) {
    val reached by remember {
        derivedStateOf {
            reachedBottom(preloadThreshold = preloadThreshold)
        }
    }
    LaunchedEffect(reached) {
        if (reached) action()
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchMembersScreenPreview() {
    MulkkamTheme {
        SearchMembersScreen(navigateToBack = {})
    }
}
