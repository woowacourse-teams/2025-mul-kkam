package com.mulkkam.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.checker.CalorieChecker
import com.mulkkam.domain.checker.CalorieChecker.Companion.DEFAULT_CHECK_CALORIE_INTERVAL_HOURS
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.VersionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val healthRepository: HealthRepository,
    private val versionsRepository: VersionsRepository,
    private val calorieChecker: CalorieChecker,
    private val logger: Logger,
) : ViewModel() {
    private val _isHealthPermissionGranted: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isHealthPermissionGranted: StateFlow<Boolean?> = _isHealthPermissionGranted.asStateFlow()

    private val _onReceiveFriendWaterBalloon: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)
    val onReceiveFriendWaterBalloon: SharedFlow<Unit> = _onReceiveFriendWaterBalloon.asSharedFlow()

    private val _isAppOutdated: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isAppOutdated: SharedFlow<Boolean> = _isAppOutdated.asSharedFlow()

    private val numericPattern = Regex("""^\d+""")

    // TODO: 옮기기
    fun checkHealthPermissions(permissions: Set<String>) {
        viewModelScope.launch {
            val granted = healthRepository.hasPermissions(permissions)
            logger.info(
                LogEvent.HEALTH_CONNECT,
                "Health Connect permissions check completed: granted=$granted",
            )
            _isHealthPermissionGranted.value = granted
        }
    }

    // TODO: 옮기기
    fun scheduleCalorieCheck() {
        calorieChecker.checkCalorie(DEFAULT_CHECK_CALORIE_INTERVAL_HOURS)
    }

    // TODO: 옮기기
    fun checkAppVersion(currentVersionName: String) {
        viewModelScope.launch {
            runCatching {
                versionsRepository.getMinimumVersion().getOrError()
            }.onSuccess { minimumVersion ->
                _isAppOutdated.emit(isOutdated(currentVersionName, minimumVersion))
            }
        }
    }

    private fun isOutdated(
        currentVersion: String,
        minimumVersion: String,
    ): Boolean {
        val currentParts = currentVersion.split(".").map { it.toNumericPart() }
        val minimumParts = minimumVersion.split(".").map { it.toNumericPart() }

        for (index in currentParts.indices) {
            val currentPart = currentParts[index]
            val minimumPart = minimumParts[index]

            if (currentPart < minimumPart) return true
            if (currentPart > minimumPart) return false
        }
        return false
    }

    // TODO: 옮기기
    fun receiveFriendWaterBalloon() {
        viewModelScope.launch {
            _onReceiveFriendWaterBalloon.emit(Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearFriendWaterBalloonEvent() {
        _onReceiveFriendWaterBalloon.resetReplayCache()
    }

    private fun String.toNumericPart(): Int = numericPattern.find(this)?.value?.toIntOrNull() ?: 0
}
