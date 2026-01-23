package com.mulkkam.ui.setting.cups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.component.MulKkamAlertDialog
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.setting.cups.adapter.SettingCupsItem
import com.mulkkam.ui.setting.cups.component.SettingCupBottomSheet
import com.mulkkam.ui.setting.cups.model.CupUiModel
import com.mulkkam.ui.setting.cups.model.CupsUiModel
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.load_info_error
import mulkkam.shared.generated.resources.network_check_error
import mulkkam.shared.generated.resources.setting_cups_reset_description
import mulkkam.shared.generated.resources.setting_cups_reset_success
import mulkkam.shared.generated.resources.setting_cups_reset_title
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val REORDER_RANK_DELAY_MILLIS: Long = 2000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingCupsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Boolean,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingCupsViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val cupsUiState by viewModel.cupsUiState.collectAsStateWithLifecycle()
    val listItems = remember { mutableStateListOf<SettingCupsItem>() }

    var isResetDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    var selectedCup by remember { mutableStateOf<CupUiModel?>(null) }
    var addSheetKey by rememberSaveable { mutableStateOf(0) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var reorderJob by remember { mutableStateOf<Job?>(null) }
    val onReorderCups: (List<CupUiModel>) -> Unit = { newOrder ->
        viewModel.applyOptimisticCupOrder(newOrder)
        reorderJob?.cancel()
        reorderJob =
            coroutineScope.launch {
                delay(REORDER_RANK_DELAY_MILLIS)
                viewModel.updateCupOrder(newOrder)
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            reorderJob?.cancel()
            reorderJob = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cupsUiState.collectWithLifecycle(lifecycleOwner) { state ->
            handleCupsUiState(
                state = state,
                listItems = listItems,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cupsResetUiState.collectWithLifecycle(lifecycleOwner) { state ->
            handleCupsResetUiState(
                state = state,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    val contentPadding =
        PaddingValues(
            start = padding.calculateStartPadding(LocalLayoutDirection.current),
            top = 0.dp,
            end = padding.calculateEndPadding(LocalLayoutDirection.current),
            bottom = padding.calculateBottomPadding(),
        )

    SettingCupsScreen(
        padding = contentPadding,
        cupsUiState = cupsUiState,
        items = listItems,
        onResetClick = { isResetDialogVisible = true },
        onEditCup = { cup ->
            snackbarHostState.currentSnackbarData?.dismiss()
            selectedCup = cup
            isBottomSheetVisible = true
        },
        onAddCup = {
            snackbarHostState.currentSnackbarData?.dismiss()
            selectedCup = null
            addSheetKey += 1
            isBottomSheetVisible = true
        },
        onReorderCups = onReorderCups,
        onBackClick = { onNavigateToBack() },
    )

    if (isResetDialogVisible) {
        MulKkamAlertDialog(
            title = stringResource(Res.string.setting_cups_reset_title),
            description = stringResource(Res.string.setting_cups_reset_description),
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
            snackbarHostState = snackbarHostState,
            viewModel = koinViewModel(key = selectedCup?.id?.toString() ?: "add-$addSheetKey"),
        )
    }
}

private suspend fun handleCupsUiState(
    state: MulKkamUiState<CupsUiModel>,
    listItems: SnapshotStateList<SettingCupsItem>,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success<CupsUiModel> -> {
            val data: CupsUiModel = state.data
            val cupItems: List<SettingCupsItem> =
                buildList {
                    addAll(data.cups.map { cup -> SettingCupsItem.CupItem(cup) })
                    if (data.isAddable) add(SettingCupsItem.AddItem)
                }
            listItems.clear()
            listItems.addAll(cupItems)
        }

        is MulKkamUiState.Failure -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.load_info_error),
                iconResource = Res.drawable.ic_alert_circle,
            )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}

private suspend fun handleCupsResetUiState(
    state: MulKkamUiState<Unit>,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success<Unit> -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.setting_cups_reset_success),
                iconResource = Res.drawable.ic_terms_all_check_on,
            )
        }

        is MulKkamUiState.Failure -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.network_check_error),
                iconResource = Res.drawable.ic_alert_circle,
            )
        }

        is MulKkamUiState.Idle, MulKkamUiState.Loading -> {
            Unit
        }
    }
}
