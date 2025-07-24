package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _currentWaterIntake = MutableLiveData<Int>()
    val currentWaterIntake: LiveData<Int> get() = _currentWaterIntake

    fun addWaterIntake(newWaterIntake: Int) {
        val addedResult = (currentWaterIntake.value ?: 0) + newWaterIntake
        _currentWaterIntake.value = newWaterIntake
    }
}
