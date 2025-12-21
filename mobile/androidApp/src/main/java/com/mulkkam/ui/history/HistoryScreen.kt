package com.mulkkam.ui.history

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.intake.WaterIntakeState
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.component.MulKkamAlertDialog
import com.mulkkam.ui.history.component.DailyWaterIntakeChart
import com.mulkkam.ui.history.component.IntakeHistoryItem
import com.mulkkam.ui.history.component.WeeklyWaterIntakeChart
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.util.LoadingShimmerEffect
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = koinViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val weeklyIntakeHistories by viewModel.weeklyIntakeHistoriesUiState.collectAsStateWithLifecycle()
    val dailyIntakeHistory by viewModel.dailyIntakeHistories.collectAsStateWithLifecycle()
    val waterIntakeState by viewModel.waterIntakeState.collectAsStateWithLifecycle()
    val isNotCurrentWeek by viewModel.isNotCurrentWeek.collectAsStateWithLifecycle()
    val deleteUiState by viewModel.deleteUiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var deletedHistory: Int? by remember { mutableStateOf(null) }

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deleteUiState) {
        when (deleteUiState) {
            is MulKkamUiState.Failure ->
                handleDeleteFailure(
                    context,
                    deleteUiState as MulKkamUiState.Failure,
                    snackbarHostState,
                )

            is MulKkamUiState.Success -> handleDeleteSuccess(context, snackbarHostState)
            else -> Unit
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        if (weeklyIntakeHistories == MulKkamUiState.Loading) {
            LoadingShimmerEffect {
                HistoryShimmerScreen(it)
            }
        }

        val weeklyIntakeHistorySummaries =
            weeklyIntakeHistories.toSuccessDataOrNull() ?: return@Box
        val date = dailyIntakeHistory.date

        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.history_view_label),
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
                    currentDate = date,
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
                    text = stringResource(R.string.history_intake_history_label),
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
                                            message = context.getString(R.string.history_delete_failure_past),
                                            iconResourceId = R.drawable.ic_alert_circle,
                                        )
                                    }
                                    return@clickable
                                }
                                deletedHistory = intakeHistory.id
                                showDialog = true
                            },
                )
            }
        }

        if (showDialog) {
            MulKkamAlertDialog(
                title = stringResource(R.string.history_delete_dialog_label),
                description = stringResource(R.string.history_delete_dialog_sub_label),
                onConfirm = {
                    viewModel.deleteIntakeHistory(
                        deletedHistory ?: return@MulKkamAlertDialog,
                    )
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }

        MulKkamSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private suspend fun handleDeleteFailure(
    context: Context,
    state: MulKkamUiState.Failure,
    snackbarHostState: SnackbarHostState,
) {
    if (state.error !is MulKkamError.HistoryError.InvalidDateForDelete) {
        snackbarHostState.showMulKkamSnackbar(
            message = context.getString(R.string.network_check_error),
            iconResourceId = R.drawable.ic_alert_circle,
        )
    } else {
        snackbarHostState.showMulKkamSnackbar(
            message = context.getString(R.string.history_delete_failure_past),
            iconResourceId = R.drawable.ic_alert_circle,
        )
    }
}

private suspend fun handleDeleteSuccess(
    context: Context,
    snackbarHostState: SnackbarHostState,
) {
    snackbarHostState.showMulKkamSnackbar(
        message = context.getString(R.string.history_delete_success),
        iconResourceId = R.drawable.ic_terms_all_check_on,
    )
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    MulKkamTheme {
        HistoryScreen()
    }
}
