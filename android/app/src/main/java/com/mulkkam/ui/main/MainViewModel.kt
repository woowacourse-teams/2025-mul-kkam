package com.mulkkam.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.healthRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isHealthPermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
    val isHealthPermissionGranted: MutableLiveData<Boolean>
        get() = _isHealthPermissionGranted

    fun requestPermissionsIfNeeded(permissions: Set<String>) {
        viewModelScope.launch {
            _isHealthPermissionGranted.value = healthRepository.hasPermissions(permissions)
        }
    }

    fun updateHealthPermissionStatus(isGranted: Boolean) {
        _isHealthPermissionGranted.value = isGranted
    }
}
