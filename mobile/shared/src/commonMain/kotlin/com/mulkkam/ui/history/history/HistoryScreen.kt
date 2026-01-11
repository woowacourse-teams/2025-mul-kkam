package com.mulkkam.ui.history.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.intake.WaterIntakeState
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamAlertDialog
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.history.HistoryViewModel
import com.mulkkam.ui.history.history.component.DailyWaterIntakeChart
import com.mulkkam.ui.history.history.component.IntakeHistoryItem
import com.mulkkam.ui.history.history.component.WeeklyWaterIntakeChart
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.util.LoadingShimmerEffect
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.history_delete_dialog_label
import mulkkam.shared.generated.resources.history_delete_dialog_sub_label
import mulkkam.shared.generated.resources.history_delete_failure_past
import mulkkam.shared.generated.resources.history_delete_success
import mulkkam.shared.generated.resources.history_intake_history_label
import mulkkam.shared.generated.resources.history_no_intake_histories
import mulkkam.shared.generated.resources.history_view_label
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_terms_all_check_on
import mulkkam.shared.generated.resources.network_check_error
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HistoryScreen(
    padding: PaddingValues,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    val weeklyIntakeHistories by viewModel.weeklyIntakeHistoriesUiState.collectAsStateWithLifecycle()
    val dailyIntakeHistory by viewModel.dailyIntakeHistories.collectAsStateWithLifecycle()
    val waterIntakeState by viewModel.waterIntakeState.collectAsStateWithLifecycle()
    val isNotCurrentWeek by viewModel.isNotCurrentWeek.collectAsStateWithLifecycle()
    val deleteUiState by viewModel.deleteUiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var deletedHistory: Int? by remember { mutableStateOf(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deleteUiState) {
        when (deleteUiState) {
            is MulKkamUiState.Failure -> {
                val state = deleteUiState as MulKkamUiState.Failure

                if (state.error !is MulKkamError.HistoryError.InvalidDateForDelete) {
                    snackbarHostState.showMulKkamSnackbar(
                        message = getString(Res.string.network_check_error),
                        iconResource = Res.drawable.ic_alert_circle,
                    )
                } else {
                    snackbarHostState.showMulKkamSnackbar(
                        message = getString(Res.string.history_delete_failure_past),
                        iconResource = Res.drawable.ic_alert_circle,
                    )
                }
            }

            is MulKkamUiState.Success -> {
                snackbarHostState.showMulKkamSnackbar(
                    message = getString(Res.string.history_delete_success),
                    iconResource = Res.drawable.ic_terms_all_check_on,
                )
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.background(White).padding(padding),
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
        containerColor = White,
    ) { innerPadding ->
        if (weeklyIntakeHistories == MulKkamUiState.Loading) {
            LoadingShimmerEffect {
                HistoryShimmerScreen(it)
            }
        }

        val weeklyIntakeHistorySummaries =
            weeklyIntakeHistories.toSuccessDataOrNull() ?: return@Scaffold
        val date = dailyIntakeHistory.date

        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.history_view_label),
                    color = Black,
                    style = MulKkamTheme.typography.headline1,
                    modifier = Modifier.padding(top = 28.dp, start = 24.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            stickyHeader {
                WeeklyWaterIntakeChart(
                    weeklyIntakeHistorySummaries = weeklyIntakeHistorySummaries,
                    onClickDate = { intakeHistorySummary ->
                        viewModel.updateDailyIntakeHistories(
                            dailySummary = intakeHistorySummary,
                        )
                    },
                    selectedDate = date,
                    onClickButton = { weeksToMove -> viewModel.moveWeek(weeksToMove) },
                    isNotCurrentWeek = isNotCurrentWeek,
                )
            }

            item {
                DailyWaterIntakeChart(
                    dailyIntakeHistory = dailyIntakeHistory,
                    waterIntakeState = waterIntakeState,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            item {
                Text(
                    text = stringResource(Res.string.history_intake_history_label),
                    style = MulKkamTheme.typography.title2,
                    color = Black,
                    modifier = Modifier.padding(top = 38.dp, start = 24.dp),
                )
            }

            item { Spacer(modifier = Modifier.height(18.dp)) }

            items(
                items = dailyIntakeHistory.intakeHistories,
                key = { it.id },
            ) { intakeHistory ->
                IntakeHistoryItem(
                    intakeHistory = intakeHistory,
                    modifier =
                        Modifier
                            .clickable {
                                if (waterIntakeState !is WaterIntakeState.Present) {
                                    coroutineScope.launch {
                                        snackbarHostState.showMulKkamSnackbar(
                                            message = getString(Res.string.history_delete_failure_past),
                                            iconResource = Res.drawable.ic_alert_circle,
                                        )
                                    }
                                    return@clickable
                                }
                                deletedHistory = intakeHistory.id
                                showDialog = true
                            },
                )
            }

            if (dailyIntakeHistory.intakeHistories.isEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.history_no_intake_histories),
                        modifier = Modifier.padding(top = 18.dp, start = 24.dp),
                        style = MulKkamTheme.typography.body4,
                        color = Black,
                    )
                }
            }
        }

        if (showDialog) {
            MulKkamAlertDialog(
                title = stringResource(Res.string.history_delete_dialog_label),
                description = stringResource(Res.string.history_delete_dialog_sub_label),
                onConfirm = {
                    viewModel.deleteIntakeHistory(
                        deletedHistory ?: return@MulKkamAlertDialog,
                    )
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    MulKkamTheme {
        HistoryScreen(padding = PaddingValues())
    }
}
