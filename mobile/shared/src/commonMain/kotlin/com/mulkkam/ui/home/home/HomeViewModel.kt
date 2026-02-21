package com.mulkkam.ui.home.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.intake.IntakeHistoryResult
import com.mulkkam.domain.model.intake.IntakeHistorySummary
import com.mulkkam.domain.model.intake.IntakeInfo
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NotificationRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val membersRepository: MembersRepository,
    private val cupsRepository: CupsRepository,
    private val notificationRepository: NotificationRepository,
    private val intakeRepository: IntakeRepository,
    private val tokenRepository: TokenRepository,
    private val devicesRepository: DevicesRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _todayProgressInfoUiState: MutableStateFlow<MulKkamUiState<TodayProgressInfo>> =
        MutableStateFlow(MulKkamUiState.Success<TodayProgressInfo>(TodayProgressInfo.EMPTY_TODAY_PROGRESS_INFO))
    val todayProgressInfoUiState: StateFlow<MulKkamUiState<TodayProgressInfo>> get() = _todayProgressInfoUiState.asStateFlow()

    private val _cupsUiState: MutableStateFlow<MulKkamUiState<Cups>> =
        MutableStateFlow(MulKkamUiState.Success<Cups>(Cups.EMPTY_CUPS))
    val cupsUiState: StateFlow<MulKkamUiState<Cups>> get() = _cupsUiState.asStateFlow()

    private val _alarmCountUiState: MutableStateFlow<MulKkamUiState<Long>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val alarmCountUiState: StateFlow<MulKkamUiState<Long>> get() = _alarmCountUiState.asStateFlow()

    private val _drinkUiState: MutableSharedFlow<MulKkamUiState<IntakeInfo>> =
        MutableSharedFlow(replay = 0, extraBufferCapacity = 1)
    val drinkUiState: SharedFlow<MulKkamUiState<IntakeInfo>> get() = _drinkUiState.asSharedFlow()

    private val _isFirstLaunch: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    private var isPostingDrink: Boolean = false
    private var latestNotificationPermission: Boolean? = null
    private var firebaseMessagingToken: String? = null

    private val _isGoalAchieved: MutableSharedFlow<Unit> = MutableSharedFlow()
    val isGoalAchieved: SharedFlow<Unit> get() = _isGoalAchieved.asSharedFlow()

    init {
        loadTodayProgressInfo()
        loadCups()
        loadAlarmCount()
        loadFirebaseMessagingToken()
        checkFirstLaunch()
    }

    fun checkFirstLaunch() {
        viewModelScope.launch {
            runCatching {
                membersRepository.getIsFirstLaunch().getOrError()
            }.onSuccess { isFirstLaunch ->
                _isFirstLaunch.value = isFirstLaunch
                if (isFirstLaunch) {
                    membersRepository.saveIsFirstLaunch()
                }
            }
        }
    }

    fun loadFirebaseMessagingToken() {
        viewModelScope.launch {
            runCatching {
                tokenRepository.getFcmToken().getOrError()
            }.onSuccess { token ->
                firebaseMessagingToken = token
            }
        }
    }

    fun updateFirebaseMessagingToken(token: String) {
        val previousToken = firebaseMessagingToken
        if (previousToken == token) return

        viewModelScope.launch {
            runCatching {
                tokenRepository.saveFcmToken(token).getOrError()
            }.onSuccess {
                firebaseMessagingToken = token
                latestNotificationPermission?.let { isCurrentlyGranted ->
                    syncNotificationPermission(isCurrentlyGranted = isCurrentlyGranted)
                }
            }
        }
    }

    fun updateNotificationPermission(isCurrentlyGranted: Boolean) {
        if (latestNotificationPermission == isCurrentlyGranted) return

        latestNotificationPermission = isCurrentlyGranted
        if (firebaseMessagingToken == null) return
        syncNotificationPermission(isCurrentlyGranted = isCurrentlyGranted)
    }

    fun logNotificationRegistrationError(errorMessage: String) {
        logger.error(
            LogEvent.PUSH_NOTIFICATION,
            "Notification registration failed: $errorMessage",
        )
    }

    private fun syncNotificationPermission(isCurrentlyGranted: Boolean) {
        viewModelScope.launch {
            val previousNotificationPermission =
                runCatching {
                    devicesRepository.getNotificationGranted().getOrError()
                }.getOrNull() ?: return@launch
            if (previousNotificationPermission == isCurrentlyGranted) return@launch

            logger.info(
                LogEvent.PUSH_NOTIFICATION,
                "Notification permission changed: isGranted=$isCurrentlyGranted",
            )

            val isServerSynchronized: Boolean =
                if (isCurrentlyGranted) {
                    registerDevice()
                } else {
                    unregisterDevice()
                }
            if (!isServerSynchronized) return@launch

            runCatching {
                devicesRepository.saveNotificationGranted(isCurrentlyGranted).getOrError()
            }
        }
    }

    private suspend fun registerDevice(): Boolean {
        val token = firebaseMessagingToken ?: return false

        return runCatching {
            devicesRepository.postDevice(fcmToken = token).getOrError()
        }.isSuccess
    }

    private suspend fun unregisterDevice(): Boolean {
        val deviceId =
            runCatching {
                devicesRepository.getDeviceUuid().getOrError()
            }.getOrNull() ?: return false

        return runCatching {
            devicesRepository.deleteDevice(deviceId).getOrError()
        }.isSuccess
    }

    @OptIn(ExperimentalTime::class)
    fun loadTodayProgressInfo() {
        if (todayProgressInfoUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _todayProgressInfoUiState.value = MulKkamUiState.Loading
                membersRepository
                    .getMembersProgressInfo(Clock.System.todayIn(TimeZone.currentSystemDefault()))
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
                cupsRepository.getCups().getOrError()
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

    @OptIn(ExperimentalTime::class)
    fun addWaterIntakeByCup(cupId: Long) {
        val cups = cupsUiState.value.toSuccessDataOrNull() ?: return
        val cup = cups.findCupById(cupId) ?: return

        if (isPostingDrink) return
        viewModelScope.launch {
            runCatching {
                logger.info(
                    LogEvent.USER_ACTION,
                    "Posting cup intake for cupId=${cup.id}",
                )
                isPostingDrink = true
                intakeRepository
                    .postIntakeHistoryCup(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), cup.id)
                    .getOrError()
            }.onSuccess { intakeHistory ->
                updateIntakeHistory(intakeHistory)
            }.onFailure {
                isPostingDrink = false
                _drinkUiState.tryEmit(MulKkamUiState.Failure(it.toMulKkamError()))
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
        isPostingDrink = false
        _drinkUiState.tryEmit(
            MulKkamUiState.Success(
                IntakeInfo(intakeHistory.intakeType, intakeHistory.intakeAmount),
            ),
        )
        if (current.achievementRate < IntakeHistorySummary.ACHIEVEMENT_RATE_MAX &&
            intakeHistory.achievementRate >= IntakeHistorySummary.ACHIEVEMENT_RATE_MAX
        ) {
            viewModelScope.launch { _isGoalAchieved.emit(Unit) }
        }
    }

    fun addWaterIntake(
        intakeType: IntakeType,
        amount: Int,
    ) {
        if (isPostingDrink) return
        runCatching {
            CupAmount(amount)
        }.onSuccess { realAmount ->
            addWaterIntake(intakeType, realAmount)
        }.onFailure {
            _drinkUiState.tryEmit(MulKkamUiState.Failure(it.toMulKkamError()))
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun addWaterIntake(
        intakeType: IntakeType,
        amount: CupAmount,
    ) {
        if (isPostingDrink) return
        viewModelScope.launch {
            runCatching {
                logger.info(
                    LogEvent.USER_ACTION,
                    "Posting manual intake type=${intakeType.name}",
                )
                isPostingDrink = true
                intakeRepository
                    .postIntakeHistoryInput(
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                        intakeType,
                        amount,
                    ).getOrError()
            }.onSuccess { intakeHistory ->
                updateIntakeHistory(intakeHistory)
            }.onFailure {
                isPostingDrink = false
                _drinkUiState.tryEmit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }
    }
}
