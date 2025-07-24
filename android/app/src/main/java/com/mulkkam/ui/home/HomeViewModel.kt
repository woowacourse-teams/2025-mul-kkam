package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.Cups
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _currentWaterIntake = MutableLiveData<Int>()
    val currentWaterIntake: LiveData<Int> get() = _currentWaterIntake
    var cups: Cups? = null

    init {

    fun addWaterIntake(newWaterIntake: Int) {
        val addedResult = (currentWaterIntake.value ?: 0) + newWaterIntake
        _currentWaterIntake.value = newWaterIntake
        viewModelScope.launch {
            cups = RepositoryInjection.cupsRepository.getCups()
        }
    }

    }
}
