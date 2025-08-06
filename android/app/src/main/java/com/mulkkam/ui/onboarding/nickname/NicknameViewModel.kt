package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import kotlinx.coroutines.launch

class NicknameViewModel : ViewModel() {
    private val _nicknameValidationState = MutableLiveData<Boolean?>()
    val nicknameValidationState: MutableLiveData<Boolean?>
        get() = _nicknameValidationState

    fun checkNicknameDuplicate(nickname: String) {
        viewModelScope.launch {
            val result =
                RepositoryInjection.membersRepository.getMembersNicknameValidation(nickname)
            runCatching {
                result.getOrError()
                _nicknameValidationState.value = true
            }.onFailure {
                _nicknameValidationState.value = false
            }
        }
    }

    fun clearNicknameValidationState() {
        _nicknameValidationState.value = null
    }
}
