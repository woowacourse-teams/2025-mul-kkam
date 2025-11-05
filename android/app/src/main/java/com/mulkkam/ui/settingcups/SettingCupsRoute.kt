package com.mulkkam.ui.settingcups

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.dialog.MulKkamAlertDialog
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.component.SettingCupsTopAppBar
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel

@Composable
fun SettingCupsRoute(
    viewModel: SettingCupsViewModel,
    onBackClick: () -> Unit,
    onConfirmReset: () -> Unit,
    onEditCup: (CupUiModel) -> Unit,
    onAddCup: () -> Unit,
    onReorderCups: (List<CupUiModel>) -> Unit,
) {
    val cupsUiState: MulKkamUiState<CupsUiModel> by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val cupsResetUiState: MulKkamUiState<Unit> by viewModel.cupsResetUiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val currentOnConfirmReset = rememberUpdatedState(newValue = onConfirmReset)
    var isResetDialogVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    val loadErrorMessage: String = stringResource(R.string.load_info_error)
    val resetSuccessMessage: String = stringResource(R.string.setting_cups_reset_success)
    val networkErrorMessage: String = stringResource(R.string.network_check_error)

    val listItems = remember { mutableStateListOf<SettingCupsItem>() }

    LaunchedEffect(cupsUiState) {
        if (cupsUiState is MulKkamUiState.Success<CupsUiModel>) {
            val data = (cupsUiState as? MulKkamUiState.Success<CupsUiModel>)?.data ?: return@LaunchedEffect
            val cupItems: List<SettingCupsItem> =
                buildList {
                    addAll(data.cups.map { cup -> SettingCupsItem.CupItem(cup) })
                    if (data.isAddable) add(SettingCupsItem.AddItem)
                }
            listItems.clear()
            listItems.addAll(cupItems)
        }
    }

    LaunchedEffect(cupsUiState) {
        if (cupsUiState is MulKkamUiState.Failure) {
            snackbarHostState.showMulKkamSnackbar(
                message = loadErrorMessage,
                iconResourceId = R.drawable.ic_alert_circle,
            )
        }
    }

    LaunchedEffect(cupsResetUiState) {
        when (cupsResetUiState) {
            is MulKkamUiState.Success ->
                snackbarHostState.showMulKkamSnackbar(
                    message = resetSuccessMessage,
                    iconResourceId = R.drawable.ic_terms_all_check_on,
                )

            is MulKkamUiState.Failure ->
                snackbarHostState.showMulKkamSnackbar(
                    message = networkErrorMessage,
                    iconResourceId = R.drawable.ic_alert_circle,
                )

            else -> Unit
        }
    }

    Scaffold(
        topBar = { SettingCupsTopAppBar(onBackClick = onBackClick) },
        containerColor = White,
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        SettingCupsScreen(
            cupsUiState = cupsUiState,
            items = listItems,
            onResetClick = { isResetDialogVisible = true },
            onEditCup = onEditCup,
            onAddCup = onAddCup,
            onReorderCups = onReorderCups,
            paddingValues = innerPadding,
        )
    }

    if (isResetDialogVisible) {
        MulKkamAlertDialog(
            title = stringResource(R.string.setting_cups_reset_title),
            description = stringResource(R.string.setting_cups_reset_description),
            onConfirm = {
                currentOnConfirmReset.value()
                isResetDialogVisible = false
            },
            onDismiss = { isResetDialogVisible = false },
        )
    }
}
