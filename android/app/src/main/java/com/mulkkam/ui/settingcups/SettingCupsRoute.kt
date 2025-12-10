package com.mulkkam.ui.settingcups

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamToastHost
import com.mulkkam.ui.component.rememberMulKkamToastState
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.dialog.MulKkamAlertDialog
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingcups.adapter.SettingCupsItem
import com.mulkkam.ui.settingcups.component.SettingCupBottomSheet
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingCupsRoute(
    onBackClick: () -> Unit,
    onReorderCups: (cups: List<CupUiModel>) -> Unit,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    viewModel: SettingCupsViewModel,
) {
    val context: Context = LocalContext.current
    val cupsUiState: MulKkamUiState<CupsUiModel> by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val cupsResetUiState: MulKkamUiState<Unit> by viewModel.cupsResetUiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    var isResetDialogVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    var isBottomSheetVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    var selectedCup: CupUiModel? by rememberSaveable { mutableStateOf(null) }
    val toastState = rememberMulKkamToastState()
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                message = context.getString(R.string.load_info_error),
                iconResourceId = R.drawable.ic_alert_circle,
            )
        }
    }

    LaunchedEffect(cupsResetUiState) {
        when (cupsResetUiState) {
            is MulKkamUiState.Success -> {
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.setting_cups_reset_success),
                    iconResourceId = R.drawable.ic_terms_all_check_on,
                )
            }

            is MulKkamUiState.Failure -> {
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.network_check_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
            }

            else -> {
                Unit
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SettingCupsScreen(
            cupsUiState = cupsUiState,
            items = listItems,
            onResetClick = { isResetDialogVisible = true },
            onEditCup = { cup ->
                selectedCup = cup
                isBottomSheetVisible = true
            },
            onAddCup = {
                selectedCup = null
                isBottomSheetVisible = true
            },
            onReorderCups = onReorderCups,
            onBackClick = onBackClick,
            snackbarHostState = snackbarHostState,
        )

        MulKkamToastHost(
            state = toastState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (isResetDialogVisible) {
        MulKkamAlertDialog(
            title = stringResource(R.string.setting_cups_reset_title),
            description = stringResource(R.string.setting_cups_reset_description),
            onConfirm = {
                viewModel.resetCups()
                isResetDialogVisible = false
            },
            onDismiss = { isResetDialogVisible = false },
        )
    }

    if (isBottomSheetVisible) {
        SettingCupBottomSheet(
            sheetState = modalBottomSheetState,
            initialCup = selectedCup,
            onDismiss = { isBottomSheetVisible = false },
            onSaved = {
                viewModel.loadCups()
                isBottomSheetVisible = false
            },
            onDeleted = {
                viewModel.loadCups()
                isBottomSheetVisible = false
            },
            onNavigateToCoffeeEncyclopedia = onNavigateToCoffeeEncyclopedia,
            toastState = toastState,
        )
    }
}
