package com.mulkkam.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.healthRepository
import com.mulkkam.di.WorkInjection.calorieScheduler
import com.mulkkam.domain.work.CalorieScheduler.Companion.DEFAULT_CHECK_CALORIE_INTERVAL_HOURS
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isHealthPermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
    val isHealthPermissionGranted: MutableLiveData<Boolean>
        get() = _isHealthPermissionGranted

    fun checkHealthPermissions(permissions: Set<String>) {
        viewModelScope.launch {
            _isHealthPermissionGranted.value = healthRepository.hasPermissions(permissions)
        }
    }

    fun updateHealthPermissionStatus(isGranted: Boolean) {
        _isHealthPermissionGranted.value = isGranted
    }

    fun scheduleCalorieCheck() {
        calorieScheduler.scheduleCalorieCheck(DEFAULT_CHECK_CALORIE_INTERVAL_HOURS)
    }
}
