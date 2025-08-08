package com.mulkkam.ui.onboarding.targetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.TargetAmount
import kotlinx.coroutines.launch

class TargetAmountViewModel : ViewModel() {
    private var _targetAmount = MutableLiveData<TargetAmount>()
    val targetAmount: LiveData<TargetAmount> get() = _targetAmount

    private val _recommendedTargetAmount = MutableLiveData<Int>()
    val recommendedTargetAmount: MutableLiveData<Int>
        get() = _recommendedTargetAmount

    private val _isTargetAmountValid = MutableLiveData<Boolean?>()
    val isTargetAmountValid: LiveData<Boolean?> get() = _isTargetAmountValid

    fun getRecommendedTargetAmount(
        gender: Gender?,
        weight: Int?,
    ) {
        viewModelScope.launch {
            val result = RepositoryInjection.intakeRepository.getIntakeAmountTargetRecommended(gender, weight)
            runCatching {
                result.data?.let {
                    _recommendedTargetAmount.value = it
                }
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun updateTargetAmount(newTargetAmount: Int?) {
        runCatching {
            newTargetAmount?.let {
                _targetAmount.value = TargetAmount(newTargetAmount)
                _isTargetAmountValid.value = true
            } ?: run {
                _isTargetAmountValid.value = null
            }
        }.onFailure {
            _isTargetAmountValid.value = false
        }
    }
}
