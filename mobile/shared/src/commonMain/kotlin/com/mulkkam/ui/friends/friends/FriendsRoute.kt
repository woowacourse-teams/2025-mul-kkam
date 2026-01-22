package com.mulkkam.ui.friends.friends

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mulkkam.domain.model.friend.Friend
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.friends.FriendsViewModel
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.flow.collectLatest
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.friends_throw_water_balloon_success
import mulkkam.shared.generated.resources.friends_water_balloon_limit_exceeded
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.network_check_error
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FriendsRoute(
    padding: PaddingValues,
    onNavigateToPendingFriends: () -> Unit,
    onNavigateToSearchMembers: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: FriendsViewModel = koinViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.throwWaterBalloonResult.collectLatest { state ->
            handleThrowWaterBalloonResult(
                state = state,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    FriendsScreen(
        padding = padding,
        onNavigateToPendingFriends = onNavigateToPendingFriends,
        onNavigateToSearchMembers = onNavigateToSearchMembers,
        viewModel = viewModel,
    )
}

private suspend fun handleThrowWaterBalloonResult(
    state: MulKkamUiState<Friend>,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success -> {
            val friend: Friend = state.data
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.friends_throw_water_balloon_success, friend.nickname),
                iconResource = Res.drawable.ic_terms_all_check_on,
            )
        }

        is MulKkamUiState.Failure -> {
            val (messageResource, iconResource) =
                if (state.error is MulKkamError.FriendsError.ReminderLimitExceeded) {
                    Res.string.friends_water_balloon_limit_exceeded to
                        Res.drawable.ic_info_circle
                } else {
                    Res.string.network_check_error to
                        Res.drawable.ic_alert_circle
                }
            snackbarHostState.showMulKkamSnackbar(
                message = getString(messageResource),
                iconResource = iconResource,
            )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}
