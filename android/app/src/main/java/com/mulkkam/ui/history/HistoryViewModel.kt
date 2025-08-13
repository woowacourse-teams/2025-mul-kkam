package com.mulkkam.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.intake.IntakeHistory
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.domain.model.intake.IntakeHistorySummary.Companion.EMPTY_DAILY_WATER_INTAKE
import com.mulkkam.domain.model.intake.WaterIntakeState
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HistoryViewModel : ViewModel() {
    private val _weeklyIntakeHistoriesUiState: MutableLiveData<MulKkamUiState<IntakeHistorySummaries>> =
        MutableLiveData(MulKkamUiState.Loading)
    val weeklyIntakeHistoriesUiState: LiveData<MulKkamUiState<IntakeHistorySummaries>> get() = _weeklyIntakeHistoriesUiState

    private val _dailyIntakeHistories: MutableLiveData<IntakeHistorySummary> = MutableLiveData(EMPTY_DAILY_WATER_INTAKE)
    val dailyIntakeHistories: LiveData<IntakeHistorySummary> get() = _dailyIntakeHistories

    private val _isNotCurrentWeek: MutableLiveData<Boolean> = MutableLiveData()
    val isNotCurrentWeek: LiveData<Boolean> get() = _isNotCurrentWeek

    private val _waterIntakeState: MutableLiveData<WaterIntakeState> = MutableLiveData()
    val waterIntakeState: LiveData<WaterIntakeState> get() = _waterIntakeState

    private val _deleteUiState = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Loading)
    val deleteUiState: LiveData<MulKkamUiState<Unit>> get() = _deleteUiState

    init {
        loadIntakeHistories()
    }

    fun loadIntakeHistories(
        referenceDate: LocalDate = LocalDate.now(),
        currentDate: LocalDate = LocalDate.now(),
    ) {
        if (weeklyIntakeHistoriesUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            val weekDates = getWeekDates(referenceDate)
            runCatching {
                _weeklyIntakeHistoriesUiState.value = MulKkamUiState.Loading
                RepositoryInjection.intakeRepository
                    .getIntakeHistory(
                        from = weekDates.first(),
                        to = weekDates.last(),
                    ).getOrError()
            }.onSuccess { weeklyIntakeHistories ->
                _weeklyIntakeHistoriesUiState.value = MulKkamUiState.Success<IntakeHistorySummaries>(weeklyIntakeHistories)
                _isNotCurrentWeek.value = weeklyIntakeHistories.lastDay < currentDate
                selectDailySummary(weekDates, weeklyIntakeHistories, currentDate)
            }.onFailure {
                _weeklyIntakeHistoriesUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun getWeekDates(targetDate: LocalDate): List<LocalDate> {
        val monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        return List(WEEK_LENGTH) { monday.plusDays(it.toLong()) }
    }

    private fun selectDailySummary(
        weekDates: List<LocalDate>,
        summaries: IntakeHistorySummaries,
        today: LocalDate,
    ) {
        val dailySummary =
            when {
                today in weekDates -> summaries.getByDateOrEmpty(today)
                else -> summaries.getByIndex(INTAKE_HISTORY_SUMMARIES_FIRST_INDEX)
            }
        updateDailyIntakeHistories(dailySummary, today)
    }

    fun updateDailyIntakeHistories(
        dailySummary: IntakeHistorySummary,
        today: LocalDate,
    ) {
        _dailyIntakeHistories.value = dailySummary
        _waterIntakeState.value = dailySummary.determineWaterIntakeState(today)
    }

    fun moveWeek(offset: Long) {
        val current = weeklyIntakeHistoriesUiState.value
        if (current !is MulKkamUiState.Success) return

        val newReferenceDate = current.data.getDateByWeekOffset(offset)
        loadIntakeHistories(newReferenceDate)
    }

    fun deleteIntakeHistory(history: IntakeHistory) {
        if (deleteUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _deleteUiState.value = MulKkamUiState.Loading
                RepositoryInjection.intakeRepository.deleteIntakeHistoryDetails(history.id).getOrError()
            }.onSuccess {
                _deleteUiState.value = MulKkamUiState.Success(Unit)
                updateIntakeHistoriesAfterDeletion(history)
            }.onFailure {
                _deleteUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun updateIntakeHistoriesAfterDeletion(history: IntakeHistory) {
        val current = weeklyIntakeHistoriesUiState.value
        if (current !is MulKkamUiState.Success) return

        val newDailySummary = dailyIntakeHistories.value?.afterDeleteHistory(history) ?: return

        _dailyIntakeHistories.value = newDailySummary

        val currentWeeklyList = current.data.intakeHistorySummaries
        val newWeeklyList =
            currentWeeklyList.map { weeklySummary ->
                if (weeklySummary.date == newDailySummary.date) {
                    newDailySummary
                } else {
                    weeklySummary
                }
            }
        _weeklyIntakeHistoriesUiState.value = MulKkamUiState.Success<IntakeHistorySummaries>(IntakeHistorySummaries(newWeeklyList))
    }

    companion object {
        private const val INTAKE_HISTORY_SUMMARIES_FIRST_INDEX: Int = 0
        private const val WEEK_LENGTH: Int = 7
    }
}
