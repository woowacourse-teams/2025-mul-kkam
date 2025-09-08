package com.mulkkam.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.CheckerInjection.calorieChecker
import com.mulkkam.di.RepositoryInjection.devicesRepository
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.di.RepositoryInjection.versionsRepository
import com.mulkkam.domain.checker.CalorieChecker.Companion.DEFAULT_CHECK_CALORIE_INTERVAL_HOURS
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isHealthPermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
    val isHealthPermissionGranted: MutableLiveData<Boolean>
        get() = _isHealthPermissionGranted

    private val _fcmToken: MutableSingleLiveData<String?> = MutableSingleLiveData(null)
    val fcmToken: SingleLiveData<String?>
        get() = _fcmToken

    private val _onFirstLaunch: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val onFirstLaunch: SingleLiveData<Unit>
        get() = _onFirstLaunch

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
            _isHealthPermissionGranted.value = healthRepository.hasPermissions(permissions)
        }
    }

    fun scheduleCalorieCheck() {
        calorieChecker.checkCalorie(DEFAULT_CHECK_CALORIE_INTERVAL_HOURS)
    }

    fun saveNotificationPermission(
        deviceId: String,
        isCurrentlyGranted: Boolean,
    ) {
        viewModelScope.launch {
            runCatching {
                devicesRepository.getNotificationGranted().getOrError()
            }.onSuccess { previouslyGranted ->
                if (previouslyGranted == isCurrentlyGranted) return@launch

                if (isCurrentlyGranted) {
                    runCatching {
                        devicesRepository
                            .postDevice(
                                fcmToken = fcmToken.getValue() ?: return@launch,
                                deviceId = deviceId,
                            ).getOrError()
                    }.onSuccess {
                        devicesRepository.saveNotificationGranted(isCurrentlyGranted)
                    }
                } else {
                    runCatching { devicesRepository.deleteDevice(deviceId) }
                    runCatching { devicesRepository.saveNotificationGranted(isCurrentlyGranted) }
                }
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

    private fun String.toNumericPart(): Int = numericPattern.find(this)?.value?.toIntOrNull() ?: 0
}
