package com.mulkkam.ui.searchmembers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mulkkam.domain.model.members.MemberSearchInfo
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.search_friends_accept_success
import mulkkam.shared.generated.resources.search_friends_request_failed
import mulkkam.shared.generated.resources.search_friends_request_success
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchMembersRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit = {},
    viewModel: SearchMembersViewModel = koinViewModel(),
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    var receivedMemberSearchInfo: MemberSearchInfo? by remember {
        mutableStateOf(null)
    }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onRequestFriends.collectWithLifecycle(lifecycleOwner) { state ->
            handleRequestFriendsAction(
                state = state,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onAcceptFriends.collectWithLifecycle(lifecycleOwner) { state ->
            handleAcceptFriendsAction(
                state = state,
                snackbarHostState = snackbarHostState,
                onFriendAccepted = onFriendAccepted,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.receivedMemberSearchInfo.collectWithLifecycle(lifecycleOwner) { state ->
            receivedMemberSearchInfo = state.toSuccessDataOrNull()
            if (receivedMemberSearchInfo != null) {
                showDialog = true
            }
        }
    }

    SearchMembersScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        receivedMemberSearchInfo = receivedMemberSearchInfo,
        showDialog = showDialog,
        onDismissDialog = { showDialog = false },
        onConfirmDialog = { memberSearchInfo: MemberSearchInfo ->
            viewModel.acceptFriendRequest(memberSearchInfo)
            showDialog = false
        },
        viewModel = viewModel,
    )
}

private suspend fun handleRequestFriendsAction(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.search_friends_request_success),
                iconResource = Res.drawable.ic_terms_all_check_on,
            )
        }

        is MulKkamUiState.Failure -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.search_friends_request_failed),
                iconResource = Res.drawable.ic_alert_circle,
            )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}

private suspend fun handleAcceptFriendsAction(
    state: MulKkamUiState<String>,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit,
) {
    when (state) {
        is MulKkamUiState.Success<String> -> {
            val nickname: String = state.toSuccessDataOrNull() ?: return
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.search_friends_accept_success, nickname),
                iconResource = Res.drawable.ic_terms_all_check_on,
            )
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
