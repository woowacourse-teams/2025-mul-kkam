package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NicknameViewModel : ViewModel() {
    private val _nicknameValidationState = MutableLiveData<Boolean?>()
    val nicknameValidationState: MutableLiveData<Boolean?>
        get() = _nicknameValidationState

    fun checkNicknameDuplicate() {
        // TODO: 서버 통신 필요
        _nicknameValidationState.value = nicknameValidationState.value?.not() == true
    }

    fun clearNicknameValidationState() {
        _nicknameValidationState.value = null
    }
}
