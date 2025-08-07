package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import kotlinx.coroutines.launch

class NicknameViewModel : ViewModel() {
    private val _isValidNickname = MutableLiveData<Boolean?>()
    val isValidNickname: MutableLiveData<Boolean?>
        get() = _isValidNickname

    fun checkNicknameDuplicate(nickname: String) {
        viewModelScope.launch {
            val result =
                RepositoryInjection.nicknameRepository.getNicknameValidation(nickname)
            runCatching {
                result.getOrError()
                _isValidNickname.value = true
            }.onFailure {
                _isValidNickname.value = false
            }
        }
    }

    fun clearNicknameValidationState() {
        _isValidNickname.value = null
    }
}
