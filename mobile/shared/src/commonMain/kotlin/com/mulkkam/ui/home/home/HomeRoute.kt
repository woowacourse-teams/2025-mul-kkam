package com.mulkkam.ui.home.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.intake.IntakeInfo
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.showMulKkamActionSnackbar
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.home.home.component.ManualDrinkBottomSheet
import com.mulkkam.ui.home.home.model.HomeUiStateHolder
import com.mulkkam.ui.home.home.model.rememberHomeUiStateHolder
import com.mulkkam.ui.main.MainViewModel
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.authorization_expired
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.load_info_error
import mulkkam.shared.generated.resources.manual_drink_coffee_why
import mulkkam.shared.generated.resources.manual_drink_network_error
import mulkkam.shared.generated.resources.manual_drink_success
import mulkkam.shared.generated.resources.manual_drink_success_coffee
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    navigateToNotification: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    parentViewModel: MainViewModel = koinViewModel(),
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isManualDrinkBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val manualDrinkBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentNavigateToLogin = rememberUpdatedState(newValue = onNavigateToLogin)
    val currentNavigateToCoffeeEncyclopedia = rememberUpdatedState(newValue = onNavigateToCoffeeEncyclopedia)

    val uiStateHolder: HomeUiStateHolder = rememberHomeUiStateHolder()

    val todayProgressInfoUiState by viewModel.todayProgressInfoUiState.collectAsStateWithLifecycle()

    LaunchedEffect(todayProgressInfoUiState) {
        handleTodayProgressFailure(
            state = todayProgressInfoUiState,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            onNavigateToLogin = currentNavigateToLogin.value,
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.drinkUiState.collectLatest { state ->
            if (state is MulKkamUiState.Success) {
                uiStateHolder.triggerDrinkAnimation()
            }
            handleDrinkUiState(
                state = state,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onNavigateToCoffeeEncyclopedia = currentNavigateToCoffeeEncyclopedia.value,
            )
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.isGoalAchieved.collectLatest {
            uiStateHolder.triggerConfettiOnce()
        }
    }

    LaunchedEffect(parentViewModel) {
        parentViewModel.onReceiveFriendWaterBalloon.collectLatest {
            uiStateHolder.triggerFriendWaterBalloonExplode()
            parentViewModel.clearFriendWaterBalloonEvent()
        }
    }

    HomeScreen(
        navigateToNotification = navigateToNotification,
        onManualDrink = { isManualDrinkBottomSheetVisible = true },
        snackbarHostState = snackbarHostState,
        uiStateHolder = uiStateHolder,
        viewModel = viewModel,
    )

    if (isManualDrinkBottomSheetVisible) {
        ManualDrinkBottomSheet(
            sheetState = manualDrinkBottomSheetState,
            onDismiss = { isManualDrinkBottomSheetVisible = false },
            onSave = { intakeType, amount ->
                viewModel.addWaterIntake(intakeType, amount)
                isManualDrinkBottomSheetVisible = false
            },
            onNavigateToCoffeeEncyclopedia = onNavigateToCoffeeEncyclopedia,
        )
    }
}

private fun handleTodayProgressFailure(
    state: MulKkamUiState<TodayProgressInfo>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onNavigateToLogin: () -> Unit,
) {
    if (state !is MulKkamUiState.Failure) return

    when (state.error) {
        is MulKkamError.AccountError,
        is MulKkamError.Unknown,
        -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.authorization_expired),
                    iconResource = Res.drawable.ic_alert_circle,
                )
            }
            onNavigateToLogin()
        }

        else -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.load_info_error),
                    iconResource = Res.drawable.ic_alert_circle,
                )
            }
        }
    }
}

private fun handleDrinkUiState(
    state: MulKkamUiState<IntakeInfo>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
) {
    when (state) {
        is MulKkamUiState.Success -> {
            showIntakeSuccessSnackbar(
                intakeType = state.data.intakeType,
                amount = state.data.amount,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onNavigateToCoffeeEncyclopedia = onNavigateToCoffeeEncyclopedia,
            )
        }

        is MulKkamUiState.Failure -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.manual_drink_network_error),
                    iconResource = Res.drawable.ic_alert_circle,
                )
            }
        }

        is MulKkamUiState.Idle, is MulKkamUiState.Loading -> {
            Unit
        }
    }
}

private fun showIntakeSuccessSnackbar(
    intakeType: IntakeType,
    amount: Int,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
) {
    when (intakeType) {
        IntakeType.WATER -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.manual_drink_success, amount),
                    iconResource = Res.drawable.ic_terms_all_check_on,
                )
            }
        }

        IntakeType.COFFEE -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamActionSnackbar(
                    message = getString(Res.string.manual_drink_success_coffee, amount),
                    iconResource = Res.drawable.ic_terms_all_check_on,
                    actionLabel = getString(Res.string.manual_drink_coffee_why),
                    onActionPerformed = onNavigateToCoffeeEncyclopedia,
                )
            }
        }

        IntakeType.UNKNOWN -> {
            Unit
        }
    }
}
