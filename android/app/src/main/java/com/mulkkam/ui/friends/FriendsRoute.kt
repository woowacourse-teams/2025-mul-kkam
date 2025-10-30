package com.mulkkam.ui.friends

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mulkkam.R
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.extensions.collectWithLifecycle

@Composable
fun FriendsRoute(
    navigateToSearch: () -> Unit,
    navigateToFriendRequests: () -> Unit,
    viewModel: FriendsViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    viewModel.throwWaterBalloonResult.collectWithLifecycle(lifecycleOwner) { state ->
        handleThrowWaterBalloonResult(
            state = state,
            context = context,
            snackbarHostState = snackbarHostState,
        )
    }

    FriendsScreen(
        navigateToSearch = navigateToSearch,
        navigateToFriendRequests = navigateToFriendRequests,
        snackbarHostState = snackbarHostState,
        viewModel = viewModel,
    )
}

private suspend fun handleThrowWaterBalloonResult(
    state: MulKkamUiState<Friend>,
    context: Context,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success -> {
            val friend: Friend = state.data
            val message: String =
                context.getString(
                    R.string.friends_throw_water_balloon_success,
                    friend.nickname,
                )
            snackbarHostState.showMulKkamSnackbar(
                message = message,
                iconResourceId = R.drawable.ic_terms_all_check_on,
            )
        }

        is MulKkamUiState.Failure -> {
            val (messageResourceId, iconResourceId) =
                if (state.error is MulKkamError.FriendsError.ReminderLimitExceeded) {
                    R.string.friends_water_balloon_limit_exceeded to R.drawable.ic_info_circle
                } else {
                    R.string.network_check_error to R.drawable.ic_alert_circle
                }
            snackbarHostState.showMulKkamSnackbar(
                message = context.getString(messageResourceId),
                iconResourceId = iconResourceId,
            )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
    }
}
