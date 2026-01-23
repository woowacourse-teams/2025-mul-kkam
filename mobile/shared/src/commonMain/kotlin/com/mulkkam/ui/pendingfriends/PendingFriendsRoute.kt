package com.mulkkam.ui.pendingfriends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.pending_friends_accept_failed
import mulkkam.shared.generated.resources.pending_friends_accept_success
import mulkkam.shared.generated.resources.pending_friends_cancel_success
import mulkkam.shared.generated.resources.pending_friends_reject_success
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PendingFriendsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit = {},
    viewModel: PendingFriendsViewModel = koinViewModel(),
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.onAcceptRequest.collectWithLifecycle(lifecycleOwner) { state: MulKkamUiState<String> ->
            handleAcceptRequestAction(
                state = state,
                snackbarHostState = snackbarHostState,
                onFriendAccepted = onFriendAccepted,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onRejectRequest.collectWithLifecycle(lifecycleOwner) { state: MulKkamUiState<Unit> ->
            handleRejectRequestAction(
                state = state,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onCancelRequest.collectWithLifecycle(lifecycleOwner) { state: MulKkamUiState<Unit> ->
            handleCancelRequestAction(
                state = state,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    PendingFriendsScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        viewModel = viewModel,
    )
}

private suspend fun handleAcceptRequestAction(
    state: MulKkamUiState<String>,
    snackbarHostState: SnackbarHostState,
    onFriendAccepted: () -> Unit,
) {
    when (state) {
        is MulKkamUiState.Success<String> -> {
            val nickname: String = state.toSuccessDataOrNull() ?: return
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.pending_friends_accept_success, nickname),
                iconResource = Res.drawable.ic_terms_all_check_on,
            )
            onFriendAccepted()
        }

        is MulKkamUiState.Failure -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.pending_friends_accept_failed),
                iconResource = Res.drawable.ic_info_circle,
            )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}

private suspend fun handleRejectRequestAction(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.pending_friends_reject_success),
                iconResource = Res.drawable.ic_terms_all_check_on,
            )
        }

        is MulKkamUiState.Failure -> {
            Unit
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}

private suspend fun handleCancelRequestAction(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.pending_friends_cancel_success),
                iconResource = Res.drawable.ic_terms_all_check_on,
            )
        }

        is MulKkamUiState.Failure -> {
            Unit
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}
