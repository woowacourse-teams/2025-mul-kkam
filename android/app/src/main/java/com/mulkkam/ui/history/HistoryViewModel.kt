package com.mulkkam.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.IntakeHistory
import com.mulkkam.domain.model.IntakeHistorySummaries
import com.mulkkam.domain.model.IntakeHistorySummary
import com.mulkkam.domain.model.WaterIntakeState
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HistoryViewModel : ViewModel() {
    private val _weeklyIntakeHistories = MutableLiveData<IntakeHistorySummaries>()
    val weeklyIntakeHistories: LiveData<IntakeHistorySummaries> get() = _weeklyIntakeHistories

    private val _dailyIntakeHistories = MutableLiveData<IntakeHistorySummary>()
    val dailyIntakeHistories: LiveData<IntakeHistorySummary> get() = _dailyIntakeHistories

    val isNotCurrentWeek: LiveData<Boolean> =
        weeklyIntakeHistories.map { intakeHistories ->
            intakeHistories.lastDay < LocalDate.now()
        }

    private val _waterIntakeState: MutableLiveData<WaterIntakeState> = MutableLiveData()
    val waterIntakeState: LiveData<WaterIntakeState> get() = _waterIntakeState

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> get() = _deleteSuccess

    init {
        loadIntakeHistories()
    }

    fun loadIntakeHistories(
        referenceDate: LocalDate = LocalDate.now(),
        currentDate: LocalDate = LocalDate.now(),
    ) {
        viewModelScope.launch {
            val weekDates = getWeekDates(referenceDate)
            val result =
                RepositoryInjection.intakeRepository.getIntakeHistory(
                    from = weekDates.first(),
                    to = weekDates.last(),
                )
            runCatching {
                result.getOrError()
            }.onSuccess { summaries ->
                _weeklyIntakeHistories.value = summaries
                updateIntakeSummary(weekDates, summaries, currentDate)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    private fun getWeekDates(targetDate: LocalDate): List<LocalDate> {
        val monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        return List(WEEK_LENGTH) { monday.plusDays(it.toLong()) }
    }

    private fun updateIntakeSummary(
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
        val newBaseDate =
            weeklyIntakeHistories.value?.getDateByWeekOffset(offset) ?: LocalDate.now()
        loadIntakeHistories(newBaseDate)
    }

    fun deleteIntakeHistory(history: IntakeHistory) {
        viewModelScope.launch {
            val result = RepositoryInjection.intakeRepository.deleteIntakeHistoryDetails(history.id)
            runCatching {
                result.getOrError()
                _deleteSuccess.value = true
            }.onFailure {
                // TODO : 에러 처리
            }
        }
    }

    fun onDeleteSuccessObserved() {
        _deleteSuccess.value = false
    }

    companion object {
        private const val INTAKE_HISTORY_SUMMARIES_FIRST_INDEX: Int = 0
        private const val WEEK_LENGTH: Int = 7
    }
}
