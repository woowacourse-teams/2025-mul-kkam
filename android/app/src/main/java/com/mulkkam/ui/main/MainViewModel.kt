package com.mulkkam.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.checker.CalorieChecker
import com.mulkkam.domain.checker.CalorieChecker.Companion.DEFAULT_CHECK_CALORIE_INTERVAL_HOURS
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.repository.DevicesRepository
import com.mulkkam.domain.repository.HealthRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.TokenRepository
import com.mulkkam.domain.repository.VersionsRepository
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val tokenRepository: TokenRepository,
        private val membersRepository: MembersRepository,
        private val healthRepository: HealthRepository,
        private val devicesRepository: DevicesRepository,
        private val versionsRepository: VersionsRepository,
        private val calorieChecker: CalorieChecker,
        private val logger: Logger,
    ) : ViewModel() {
        private val _isHealthPermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
        val isHealthPermissionGranted: MutableLiveData<Boolean>
            get() = _isHealthPermissionGranted

        private val _fcmToken: MutableSingleLiveData<String?> = MutableSingleLiveData(null)
        val fcmToken: SingleLiveData<String?>
            get() = _fcmToken

        private val _onFirstLaunch: MutableSingleLiveData<Unit> = MutableSingleLiveData()
        val onFirstLaunch: SingleLiveData<Unit>
            get() = _onFirstLaunch

        private val _onReceiveFriendWaterBalloon: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)
        val onReceiveFriendWaterBalloon: SharedFlow<Unit>
            get() = _onReceiveFriendWaterBalloon.asSharedFlow()

        private val _isAppOutdated: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
        val isAppOutdated: SingleLiveData<Boolean> get() = _isAppOutdated

        private val numericPattern = Regex("""^\d+""")

        init {
            getFcmToken()
            checkFirstLaunch()
        }

        private fun getFcmToken() {
            viewModelScope.launch {
                runCatching {
                    tokenRepository.getFcmToken().getOrError()
                }.onSuccess { token ->
                    _fcmToken.postValue(token)
                }
            }
        }

        private fun checkFirstLaunch() {
            viewModelScope.launch {
                runCatching {
                    membersRepository.getIsFirstLaunch().getOrError()
                }.onSuccess { isFirstLaunch ->
                    if (isFirstLaunch) {
                        _onFirstLaunch.setValue(Unit)
                        membersRepository.saveIsFirstLaunch()
                    }
                }
            }
        }

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

        fun scheduleCalorieCheck() {
            calorieChecker.checkCalorie(DEFAULT_CHECK_CALORIE_INTERVAL_HOURS)
        }

        fun saveNotificationPermission(isCurrentlyGranted: Boolean) {
            viewModelScope.launch {
                val previouslyGranted =
                    runCatching {
                        devicesRepository.getNotificationGranted().getOrError()
                    }.getOrNull() ?: return@launch
                val deviceId =
                    runCatching {
                        devicesRepository.getDeviceUuid().getOrError()
                    }.getOrNull() ?: return@launch

                if (previouslyGranted == isCurrentlyGranted) return@launch

                logger.info(
                    LogEvent.PUSH_NOTIFICATION,
                    "Notification permission changed: isGranted=$isCurrentlyGranted",
                )

                if (isCurrentlyGranted) {
                    handlePermissionGranted(isCurrentlyGranted)
                } else {
                    handlePermissionNotGranted(deviceId, isCurrentlyGranted)
                }
            }
        }

        private fun handlePermissionGranted(isGranted: Boolean) {
            viewModelScope.launch {
                val token = fcmToken.getValue() ?: return@launch
                runCatching {
                    devicesRepository
                        .postDevice(fcmToken = token)
                        .getOrError()
                }.onSuccess {
                    devicesRepository.saveNotificationGranted(isGranted)
                }
            }
        }

        private fun handlePermissionNotGranted(
            deviceId: String,
            isGranted: Boolean,
        ) {
            viewModelScope.launch {
                runCatching {
                    devicesRepository.deleteDevice(deviceId)
                }.onSuccess {
                    devicesRepository.saveNotificationGranted(isGranted)
                }
            }
        }

        fun checkAppVersion(currentVersionName: String) {
            viewModelScope.launch {
                runCatching {
                    versionsRepository.getMinimumVersion().getOrError()
                }.onSuccess { minimumVersion ->
                    _isAppOutdated.setValue(isOutdated(currentVersionName, minimumVersion))
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

        fun receiveFriendWaterBalloon() {
            viewModelScope.launch {
                _onReceiveFriendWaterBalloon.emit(Unit)
            }
        }

        private fun String.toNumericPart(): Int = numericPattern.find(this)?.value?.toIntOrNull() ?: 0
    }
