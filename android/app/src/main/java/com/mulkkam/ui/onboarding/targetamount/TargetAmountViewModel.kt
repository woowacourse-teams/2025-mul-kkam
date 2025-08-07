package com.mulkkam.ui.onboarding.targetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.domain.model.TargetAmount

class TargetAmountViewModel : ViewModel() {
    private var _targetAmount = MutableLiveData<TargetAmount>()
    val targetAmount: LiveData<TargetAmount> get() = _targetAmount

    private val _recommendedTargetAmount = MutableLiveData<Int>()
    val recommendedTargetAmount: MutableLiveData<Int>
        get() = _recommendedTargetAmount

    private val _isTargetAmountValid = MutableLiveData<Boolean?>()
    val isTargetAmountValid: LiveData<Boolean?> get() = _isTargetAmountValid

    init {
        // TODO: 추천 음용량 조회 API 호출 후 갱신 필요
        _recommendedTargetAmount.value = 1800
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
