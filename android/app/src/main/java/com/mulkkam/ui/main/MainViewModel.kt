package com.mulkkam.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.devicesRepository
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
import com.mulkkam.di.RepositoryInjection.versionsRepository
import com.mulkkam.di.WorkInjection.calorieScheduler
import com.mulkkam.domain.work.CalorieScheduler.Companion.DEFAULT_CHECK_CALORIE_INTERVAL_HOURS
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
        calorieScheduler.scheduleCalorieCheck(DEFAULT_CHECK_CALORIE_INTERVAL_HOURS)
    }

    fun saveDeviceInfo(deviceId: String) {
        viewModelScope.launch {
            runCatching {
                devicesRepository
                    .postDevice(
                        fcmToken = fcmToken.getValue() ?: return@launch,
                        deviceId = deviceId,
                    ).getOrError()
            }.onSuccess {
                tokenRepository.deleteFcmToken()
            }
        }
    }

    fun checkAppVersion(currentVersionName: String) {
        viewModelScope.launch {
            runCatching {
                versionsRepository.getMinimumVersion().getOrError()
            }.onSuccess { minimumVersion ->
                _isAppOutdated.setValue(isOutdated(currentVersionName, minimumVersion))
            }.onFailure {
                _isAppOutdated.setValue(true)
            }
        }
    }

    private fun isOutdated(
        currentVersion: String,
        minimumVersion: String,
    ): Boolean {
        val currentParts: List<Int> = currentVersion.split(".").mapNotNull { it.toIntOrNull() }
        val minimumParts: List<Int> = minimumVersion.split(".").mapNotNull { it.toIntOrNull() }
        val maxLength: Int = maxOf(currentParts.size, minimumParts.size)

        for (index in 0 until maxLength) {
            val currentPart: Int = currentParts.getOrElse(index) { 0 }
            val minimumPart: Int = minimumParts.getOrElse(index) { 0 }

            when {
                currentPart < minimumPart -> return true
                currentPart > minimumPart -> return false
            }
        }
        return false
    }
}
