package com.mulkkam.ui.home

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.domain.model.intake.IntakeInfo
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamToastState
import com.mulkkam.ui.component.rememberMulKkamToastState
import com.mulkkam.ui.component.showMulKkamActionSnackbar
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.home.component.ManualDrinkBottomSheet
import com.mulkkam.ui.main.MainViewModel
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    navigateToNotification: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    parentViewModel: MainViewModel = viewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val toastState: MulKkamToastState = rememberMulKkamToastState()
    val coroutineScope = rememberCoroutineScope()
    var isManualDrinkBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val manualDrinkBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentNavigateToLogin = rememberUpdatedState(newValue = onNavigateToLogin)
    val currentNavigateToCoffeeEncyclopedia = rememberUpdatedState(newValue = onNavigateToCoffeeEncyclopedia)

    viewModel.todayProgressInfoUiState.collectWithLifecycle(lifecycleOwner) { state ->
        handleTodayProgressFailure(
            state = state,
            context = context,
            snackbarHostState = snackbarHostState,
            toastState = toastState,
            coroutineScope = coroutineScope,
            onNavigateToLogin = currentNavigateToLogin.value,
        )
    }

    viewModel.drinkUiState.collectWithLifecycle(lifecycleOwner) { state ->
        handleDrinkUiState(
            state = state,
            context = context,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            onNavigateToCoffeeEncyclopedia = currentNavigateToCoffeeEncyclopedia.value,
        )
    }

    HomeScreen(
        navigateToNotification = navigateToNotification,
        onManualDrink = { isManualDrinkBottomSheetVisible = true },
        snackbarHostState = snackbarHostState,
        toastState = toastState,
        viewModel = viewModel,
        parentViewModel = parentViewModel,
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

private suspend fun handleTodayProgressFailure(
    state: MulKkamUiState<TodayProgressInfo>,
    context: Context,
    snackbarHostState: SnackbarHostState,
    toastState: MulKkamToastState,
    coroutineScope: CoroutineScope,
    onNavigateToLogin: () -> Unit,
) {
    if (state !is MulKkamUiState.Failure) return

    when (state.error) {
        is MulKkamError.AccountError,
        is MulKkamError.Unknown,
        -> {
            coroutineScope.launch {
                toastState.showMulKkamToast(
                    message = context.getString(R.string.authorization_expired),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
            }
            onNavigateToLogin()
        }

        else -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.load_info_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
            }
        }
    }
}

private suspend fun handleDrinkUiState(
    state: MulKkamUiState<IntakeInfo>,
    context: Context,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
) {
    when (state) {
        is MulKkamUiState.Success -> {
            showIntakeSuccessSnackbar(
                intakeType = state.data.intakeType,
                amount = state.data.amount,
                context = context,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onNavigateToCoffeeEncyclopedia = onNavigateToCoffeeEncyclopedia,
            )
        }

        is MulKkamUiState.Failure -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.manual_drink_network_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
            }
        }

        MulKkamUiState.Idle,
        MulKkamUiState.Loading,
        -> {
            Unit
        }
    }
}

private fun showIntakeSuccessSnackbar(
    intakeType: IntakeType,
    amount: Int,
    context: Context,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
) {
    when (intakeType) {
        IntakeType.WATER -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.manual_drink_success, amount),
                    iconResourceId = R.drawable.ic_terms_all_check_on,
                )
            }
        }

        IntakeType.COFFEE -> {
            coroutineScope.launch {
                snackbarHostState.showMulKkamActionSnackbar(
                    message = context.getString(R.string.manual_drink_success_coffee, amount),
                    iconResourceId = R.drawable.ic_terms_all_check_on,
                    actionLabel = context.getString(R.string.manual_drink_coffee_why),
                    onActionPerformed = onNavigateToCoffeeEncyclopedia,
                )
            }
        }

        IntakeType.UNKNOWN -> {
            Unit
        }
    }
}
