package com.mulkkam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.cups.Cups.Companion.EMPTY_CUPS
import com.mulkkam.domain.model.intake.IntakeHistoryResult
import com.mulkkam.domain.model.intake.IntakeHistorySummary.Companion.ACHIEVEMENT_RATE_MAX
import com.mulkkam.domain.model.intake.IntakeInfo
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.members.TodayProgressInfo.Companion.EMPTY_TODAY_PROGRESS_INFO
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _todayProgressInfoUiState: MutableStateFlow<MulKkamUiState<TodayProgressInfo>> =
        MutableStateFlow(MulKkamUiState.Success<TodayProgressInfo>(EMPTY_TODAY_PROGRESS_INFO))
    val todayProgressInfoUiState: StateFlow<MulKkamUiState<TodayProgressInfo>> get() = _todayProgressInfoUiState.asStateFlow()

    private val _cupsUiState: MutableStateFlow<MulKkamUiState<Cups>> =
        MutableStateFlow(MulKkamUiState.Success<Cups>(EMPTY_CUPS))
    val cupsUiState: StateFlow<MulKkamUiState<Cups>> get() = _cupsUiState.asStateFlow()

    private val _alarmCountUiState: MutableStateFlow<MulKkamUiState<Long>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val alarmCountUiState: StateFlow<MulKkamUiState<Long>> get() = _alarmCountUiState.asStateFlow()

    private val _drinkUiState: MutableStateFlow<MulKkamUiState<IntakeInfo>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val drinkUiState: StateFlow<MulKkamUiState<IntakeInfo>> get() = _drinkUiState.asStateFlow()

    private val _isGoalAchieved: MutableSharedFlow<Unit> = MutableSharedFlow()
    val isGoalAchieved: SharedFlow<Unit> get() = _isGoalAchieved.asSharedFlow()

    init {
        loadTodayProgressInfo()
        loadCups()
        loadAlarmCount()
    }

    fun loadTodayProgressInfo() {
        if (todayProgressInfoUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _todayProgressInfoUiState.value = MulKkamUiState.Loading
                RepositoryInjection.membersRepository
                    .getMembersProgressInfo(LocalDate.now())
                    .getOrError()
            }.onSuccess { todayProgressInfoUiState ->
                _todayProgressInfoUiState.value =
                    MulKkamUiState.Success<TodayProgressInfo>(todayProgressInfoUiState)
            }.onFailure {
                _todayProgressInfoUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun loadCups() {
        if (cupsUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _cupsUiState.value = MulKkamUiState.Loading
                RepositoryInjection.cupsRepository.getCups().getOrError()
            }.onSuccess { cupsUiState ->
                _cupsUiState.value = MulKkamUiState.Success<Cups>(cupsUiState)
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun loadAlarmCount() {
        if (alarmCountUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _alarmCountUiState.value = MulKkamUiState.Loading
                notificationRepository.getNotificationsUnreadCount().getOrError()
            }.onSuccess { count ->
                _alarmCountUiState.value = MulKkamUiState.Success<Long>(count)
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun addWaterIntakeByCup(cupId: Long) {
        val cups = cupsUiState.value.toSuccessDataOrNull() ?: return
        val cup = cups.findCupById(cupId) ?: return

        if (drinkUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                mulKkamLogger.info(
                    LogEvent.USER_ACTION,
                    "Posting cup intake for cupId=${cup.id}",
                )
                _drinkUiState.value = MulKkamUiState.Loading
                intakeRepository
                    .postIntakeHistoryCup(LocalDateTime.now(), cup.id)
                    .getOrError()
            }.onSuccess { intakeHistory ->
                updateIntakeHistory(intakeHistory)
            }.onFailure {
                _drinkUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    private fun updateIntakeHistory(intakeHistory: IntakeHistoryResult) {
        val current = todayProgressInfoUiState.value.toSuccessDataOrNull() ?: return
        _todayProgressInfoUiState.value =
            MulKkamUiState.Success(
                current.updateProgressInfo(
                    amountDelta = intakeHistory.intakeAmount,
                    achievementRate = intakeHistory.achievementRate,
                    comment = intakeHistory.comment,
                ),
            )
        _drinkUiState.value =
            MulKkamUiState.Success(IntakeInfo(intakeHistory.intakeType, intakeHistory.intakeAmount))
        if (current.achievementRate < ACHIEVEMENT_RATE_MAX && intakeHistory.achievementRate >= ACHIEVEMENT_RATE_MAX) {
            viewModelScope.launch { _isGoalAchieved.emit(Unit) }
        }
    }

    fun addWaterIntake(
        intakeType: IntakeType,
        amount: Int,
    ) {
        if (drinkUiState.value is MulKkamUiState.Loading) return
        runCatching {
            CupAmount(amount)
        }.onSuccess { realAmount ->
            addWaterIntake(intakeType, realAmount)
        }.onFailure {
            _drinkUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
        }
    }

    private fun addWaterIntake(
        intakeType: IntakeType,
        amount: CupAmount,
    ) {
        if (drinkUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                mulKkamLogger.info(
                    LogEvent.USER_ACTION,
                    "Posting manual intake type=${intakeType.name}",
                )
                _drinkUiState.value = MulKkamUiState.Loading
                intakeRepository
                    .postIntakeHistoryInput(LocalDateTime.now(), intakeType, amount)
                    .getOrError()
            }.onSuccess { intakeHistory ->
                updateIntakeHistory(intakeHistory)
            }.onFailure {
                _drinkUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
