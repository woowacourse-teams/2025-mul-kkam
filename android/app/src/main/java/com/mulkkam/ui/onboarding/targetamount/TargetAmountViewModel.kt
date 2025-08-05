package com.mulkkam.ui.onboarding.targetamount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TargetAmountViewModel : ViewModel() {
    private val _recommendedTargetAmount = MutableLiveData<Int>()
    val recommendedTargetAmount: MutableLiveData<Int>
        get() = _recommendedTargetAmount

    init {
        // TODO: 추천 음용량 조회 API 호출 후 갱신 필요
        _recommendedTargetAmount.value = 1800
    }
}
