package com.mulkkam.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.devicesRepository
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.tokenRepository
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
}
