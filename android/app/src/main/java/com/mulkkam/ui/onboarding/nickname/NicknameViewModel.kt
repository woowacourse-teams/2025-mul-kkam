package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.nicknameRepository
import com.mulkkam.domain.model.MulKkamError.NicknameError
import com.mulkkam.domain.model.Nickname
import com.mulkkam.ui.model.NicknameValidationState
import com.mulkkam.ui.util.MutableSingleLiveData
import kotlinx.coroutines.launch

class NicknameViewModel : ViewModel() {
    private val _nicknameValidationState: MutableLiveData<NicknameValidationState> = MutableLiveData()
    val nicknameValidationState: MutableLiveData<NicknameValidationState>
        get() = _nicknameValidationState

    private val _onNicknameValidationError: MutableSingleLiveData<NicknameError> = MutableSingleLiveData()
    val onNicknameValidationError: MutableSingleLiveData<NicknameError>
        get() = _onNicknameValidationError

    fun validateNickname(nickname: String) {
        runCatching {
            Nickname(nickname)
            _nicknameValidationState.value = NicknameValidationState.PENDING_SERVER_VALIDATION
        }.onFailure {
            _nicknameValidationState.value = NicknameValidationState.INVALID
            _onNicknameValidationError.setValue(it as NicknameError)
        }
    }

    // 서버로부터 닉네임 검증
    fun checkNicknameUsability(nickname: String) {
        viewModelScope.launch {
            val result =
                nicknameRepository.getNicknameValidation(nickname)
            runCatching {
                result.getOrError()
                _nicknameValidationState.value = NicknameValidationState.VALID
            }.onFailure {
                _nicknameValidationState.value = NicknameValidationState.INVALID
                _onNicknameValidationError.setValue(it as NicknameError)
            }
        }
    }
}
