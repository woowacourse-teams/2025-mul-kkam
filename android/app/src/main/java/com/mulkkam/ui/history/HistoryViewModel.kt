package com.mulkkam.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.IntakeHistorySummary
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HistoryViewModel : ViewModel() {
    private val _weeklyIntakeHistories = MutableLiveData<List<IntakeHistorySummary>>()
    val weeklyIntakeHistories: LiveData<List<IntakeHistorySummary>> get() = _weeklyIntakeHistories

    private val _dailyIntakeHistories = MutableLiveData<IntakeHistorySummary>()
    val dailyIntakeHistories: LiveData<IntakeHistorySummary> get() = _dailyIntakeHistories

    init {
        initIntakeHistories()
    }

    private fun initIntakeHistories() {
        viewModelScope.launch {
            val weekDates = getCurrentWeekDates()
            val summaries =
                RepositoryInjection.intakeRepository.getIntakeHistory(
                    from = weekDates.first(),
                    to = weekDates.last(),
                )

            updateIntakeSummary(weekDates, summaries)
        }
    }

    private fun getCurrentWeekDates(): List<LocalDate> {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return List(WEEK_LENGTH) { monday.plusDays(it.toLong()) }
    }

    private fun updateIntakeSummary(
        weekDates: List<LocalDate>,
        summaries: List<IntakeHistorySummary>,
    ) {
        val completedWeekIntake =
            weekDates.map { date ->
                summaries.find { it.date == date }
                    ?: IntakeHistorySummary.EMPTY_DAILY_WATER_INTAKE.copy(date = date)
            }

        _weeklyIntakeHistories.value = completedWeekIntake
        if (weekDates.contains(LocalDate.now())) {
            _dailyIntakeHistories.value = completedWeekIntake.find { it.date == LocalDate.now() }
        } else {
            _dailyIntakeHistories.value = completedWeekIntake.first()
        }
    }

    fun updateDailyIntakeHistories(dailyIntakeHistories: IntakeHistorySummary) {
        _dailyIntakeHistories.value = dailyIntakeHistories
    }

    companion object {
        private const val WEEK_LENGTH: Int = 7
    }
}
