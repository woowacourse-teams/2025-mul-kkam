package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NicknameViewModel : ViewModel() {
    private val _isValid = MutableLiveData<Boolean>()
    val isValid: MutableLiveData<Boolean>
        get() = _isValid

    fun checkNicknameDuplicate() {
        // TODO: 서버 통신 필요
        _isValid.value = isValid.value?.not() == true
    }
}
