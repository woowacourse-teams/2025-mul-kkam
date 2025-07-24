package com.mulkkam.ui.record

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

class RecordViewModel : ViewModel() {
    private val _weeklyWaterIntake = MutableLiveData<List<IntakeHistorySummary>>()
    val weeklyWaterIntake: LiveData<List<IntakeHistorySummary>> get() = _weeklyWaterIntake

    private val _dailyWaterIntake = MutableLiveData<IntakeHistorySummary>()
    val dailyWaterIntake: LiveData<IntakeHistorySummary> get() = _dailyWaterIntake

    init {
        initWaterIntake()
    }

    private fun initWaterIntake() {
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

        _weeklyWaterIntake.value = completedWeekIntake
        _dailyWaterIntake.value = completedWeekIntake.find { it.date == LocalDate.now() }
    }

    fun updateDailyWaterIntake(dailyWaterIntake: IntakeHistorySummary) {
        _dailyWaterIntake.value = dailyWaterIntake
    }

    companion object {
        private const val WEEK_LENGTH: Int = 7
    }
}
