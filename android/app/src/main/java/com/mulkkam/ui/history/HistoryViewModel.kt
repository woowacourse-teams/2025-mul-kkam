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

    val isToday: LiveData<Boolean> =
        dailyIntakeHistories.map { intakeHistory ->
            intakeHistory.date == LocalDate.now()
        }

    val isPastEmptyRecord: LiveData<Boolean> =
        dailyIntakeHistories.map { intakeHistory ->
            intakeHistory.date < LocalDate.now() && intakeHistory.totalIntakeAmount == INTAKE_AMOUNT_EMPTY
        }

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> get() = _deleteSuccess

    init {
        loadIntakeHistories()
    }

    fun loadIntakeHistories(baseDate: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val weekDates = getWeekDates(baseDate)
            val result =
                RepositoryInjection.intakeRepository.getIntakeHistory(
                    from = weekDates.first(),
                    to = weekDates.last(),
                )
            runCatching {
                val summaries = result.getOrError()
                updateIntakeSummary(weekDates, summaries)
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
    ) {
        val today = LocalDate.now()

        _weeklyIntakeHistories.value = summaries
        if (weekDates.contains(today)) {
            _dailyIntakeHistories.value = summaries.getByDateOrEmpty(today)
        } else {
            _dailyIntakeHistories.value = summaries.getByIndex(INTAKE_HISTORY_SUMMARIES_FIRST_INDEX)
        }
    }

    fun updateDailyIntakeHistories(dailyIntakeHistories: IntakeHistorySummary) {
        _dailyIntakeHistories.value = dailyIntakeHistories
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
        private const val INTAKE_AMOUNT_EMPTY: Int = 0
        private const val WEEK_LENGTH: Int = 7
    }
}
