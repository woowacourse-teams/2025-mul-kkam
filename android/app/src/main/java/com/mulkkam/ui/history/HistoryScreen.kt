package com.mulkkam.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.dialog.MulKkamAlertDialog
import com.mulkkam.ui.history.component.DailyWaterIntakeChart
import com.mulkkam.ui.history.component.IntakeHistoryItem
import com.mulkkam.ui.history.component.WeeklyWaterIntakeChart
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val weeklyIntakeHistories by viewModel.weeklyIntakeHistoriesUiState.collectAsStateWithLifecycle()
    val dailyIntakeHistory by viewModel.dailyIntakeHistories.collectAsStateWithLifecycle()
    val waterIntakeState by viewModel.waterIntakeState.collectAsStateWithLifecycle()
    val isNotCurrentWeek by viewModel.isNotCurrentWeek.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var deletedHistory: Int? by remember { mutableStateOf(null) }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize(),
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
            val weeklyIntakeHistorySummaries =
                weeklyIntakeHistories.toSuccessDataOrNull() ?: return@stickyHeader
            val date = dailyIntakeHistory.date

            WeeklyWaterIntakeChart(
                weeklyIntakeHistorySummaries = weeklyIntakeHistorySummaries,
                onClickDate = { intakeHistorySummary ->
                    viewModel.updateDailyIntakeHistories(
                        dailySummary = intakeHistorySummary,
                        today = LocalDate.now(),
                    )
                },
                currentDate = date,
                onClickButton = { offset -> viewModel.moveWeek(offset) },
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

        items(dailyIntakeHistory.intakeHistories.size) { index ->
            val asdf = dailyIntakeHistory.intakeHistories[index]
            IntakeHistoryItem(
                intakeHistory = asdf,
                modifier =
                    Modifier.clickable {
                        deletedHistory = asdf.id
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
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    MulkkamTheme {
        HistoryScreen()
    }
}
