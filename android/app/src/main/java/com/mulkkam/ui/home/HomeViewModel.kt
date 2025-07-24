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

        viewModelScope.launch {
            cups = RepositoryInjection.cupsRepository.getCups()
        }
    }

    fun addWaterIntake() {
        // TODO: 현재 cupRank가 2부터 들어가있음
        val cup = cups?.cups?.find { it.cupRank == 2 }
        val cupAmount = cup?.cupAmount
        _currentWaterIntake.value =
            (currentWaterIntake.value ?: 0) + (cupAmount ?: 0)
    }
}
