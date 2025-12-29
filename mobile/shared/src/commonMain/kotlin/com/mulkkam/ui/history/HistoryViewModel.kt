package com.mulkkam.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.intake.IntakeHistorySummaries
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.domain.model.intake.WaterIntakeState
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HistoryViewModel(
    private val intakeRepository: IntakeRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _weeklyIntakeHistoriesUiState: MutableStateFlow<MulKkamUiState<IntakeHistorySummaries>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val weeklyIntakeHistoriesUiState: StateFlow<MulKkamUiState<IntakeHistorySummaries>>
        get() = _weeklyIntakeHistoriesUiState.asStateFlow()

    @OptIn(ExperimentalTime::class)
    private val _dailyIntakeHistories: MutableStateFlow<IntakeHistorySummary> =
        MutableStateFlow(IntakeHistorySummary.createEmpty(Clock.System.todayIn(TimeZone.currentSystemDefault())))
    val dailyIntakeHistories: StateFlow<IntakeHistorySummary> get() = _dailyIntakeHistories.asStateFlow()

    private val _isNotCurrentWeek: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNotCurrentWeek: StateFlow<Boolean> get() = _isNotCurrentWeek.asStateFlow()

    private val _waterIntakeState: MutableStateFlow<WaterIntakeState> =
        MutableStateFlow(WaterIntakeState.Present.NotFull)
    val waterIntakeState: StateFlow<WaterIntakeState> get() = _waterIntakeState.asStateFlow()

    private val _deleteUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val deleteUiState: StateFlow<MulKkamUiState<Unit>> get() = _deleteUiState

    init {
        loadIntakeHistories()
    }

    @OptIn(ExperimentalTime::class)
    fun loadIntakeHistories(
        referenceDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        currentDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    ) {
        if (weeklyIntakeHistoriesUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            val weekDates = getWeekDates(referenceDate)
            runCatching {
                _weeklyIntakeHistoriesUiState.value = MulKkamUiState.Loading
                intakeRepository
                    .getIntakeHistory(
                        from = weekDates.first(),
                        to = weekDates.last(),
                    ).getOrError()
            }.onSuccess { weeklyIntakeHistories ->
                _weeklyIntakeHistoriesUiState.value =
                    MulKkamUiState.Success<IntakeHistorySummaries>(weeklyIntakeHistories)
                _isNotCurrentWeek.value = weeklyIntakeHistories.lastDay < currentDate
                selectDailySummary(weekDates, weeklyIntakeHistories, currentDate)
            }.onFailure {
                _weeklyIntakeHistoriesUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun getWeekDates(targetDate: LocalDate): List<LocalDate> {
        val daysFromMonday = (targetDate.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal + WEEK_LENGTH) % WEEK_LENGTH
        val monday = targetDate.minus(daysFromMonday, DateTimeUnit.DAY)

        return List(WEEK_LENGTH) { monday.plus(it, DateTimeUnit.DAY) }
    }

    private fun selectDailySummary(
        weekDates: List<LocalDate>,
        summaries: IntakeHistorySummaries,
        today: LocalDate,
    ) {
        val dailySummary =
            when {
                today in weekDates -> summaries.getByDateOrEmpty(today)
                else -> summaries.getByIndex(INTAKE_HISTORY_SUMMARIES_FIRST_INDEX, today)
            }
        updateDailyIntakeHistories(dailySummary, today)
    }

    @OptIn(ExperimentalTime::class)
    fun updateDailyIntakeHistories(
        dailySummary: IntakeHistorySummary,
        today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    ) {
        _dailyIntakeHistories.value = dailySummary
        _waterIntakeState.value = dailySummary.determineWaterIntakeState(today)
    }

    fun moveWeek(weeksToMove: Long) {
        val current = weeklyIntakeHistoriesUiState.value
        if (current !is MulKkamUiState.Success) return

        val newReferenceDate = current.data.getDateByWeekOffset(weeksToMove)
        loadIntakeHistories(newReferenceDate)
    }

    fun deleteIntakeHistory(historyId: Int) {
        if (deleteUiState.value is MulKkamUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                logger.info(
                    LogEvent.USER_ACTION,
                    "Confirmed delete for intake history id=$historyId",
                )
                _deleteUiState.value = MulKkamUiState.Loading
                intakeRepository
                    .deleteIntakeHistoryDetails(historyId)
                    .getOrError()
            }.onSuccess {
                _deleteUiState.value = MulKkamUiState.Success(Unit)
                updateIntakeHistoriesAfterDeletion(historyId)
            }.onFailure {
                _deleteUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun updateIntakeHistoriesAfterDeletion(historyId: Int) {
        val current = weeklyIntakeHistoriesUiState.value
        if (current !is MulKkamUiState.Success) return

        val newDailySummary = dailyIntakeHistories.value.afterDeleteHistory(historyId)

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
        _weeklyIntakeHistoriesUiState.value =
            MulKkamUiState.Success(IntakeHistorySummaries(newWeeklyList))
    }

    companion object {
        private const val INTAKE_HISTORY_SUMMARIES_FIRST_INDEX: Int = 0
        private const val WEEK_LENGTH: Int = 7
    }
}
