package com.mulkkam.ui.settingnickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingNicknameViewModel : ViewModel() {
    private val _nicknameValidationState = MutableLiveData<Boolean?>()
    val nicknameValidationState: MutableLiveData<Boolean?>
        get() = _nicknameValidationState

    fun checkNicknameDuplicate() {
        // TODO: 중복 확인 API 연결
        _nicknameValidationState.value = nicknameValidationState.value?.not() == true
    }

    fun clearNicknameValidationState() {
        _nicknameValidationState.value = null
    }

    fun saveNickname() {
        // TODO: 닉네임 저장 API 연결
    }
}
