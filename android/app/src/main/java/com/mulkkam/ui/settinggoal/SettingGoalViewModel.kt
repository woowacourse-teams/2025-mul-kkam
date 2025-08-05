package com.mulkkam.ui.settinggoal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.intakeRepository
import kotlinx.coroutines.launch

class SettingGoalViewModel : ViewModel() {
    private var _goal: MutableLiveData<Int> = MutableLiveData(0)
    val goal: LiveData<Int> get() = _goal

    private val _success: MutableLiveData<Boolean> = MutableLiveData(false)
    val success: LiveData<Boolean> get() = _success

    fun updateGoal(newGoal: Int) {
        _goal.value = newGoal
    }

    fun saveGoal() {
        viewModelScope.launch {
            val result = goal.value?.let { intakeRepository.patchIntakeTarget(it) }
            if (result?.isSuccess == true) {
                _success.value = true
            } else {
                // TODO: 에러 처리
            }
        }
    }
}
