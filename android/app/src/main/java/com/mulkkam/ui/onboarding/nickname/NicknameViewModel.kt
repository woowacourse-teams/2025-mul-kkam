package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.nicknameRepository
import com.mulkkam.domain.model.Nickname
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.model.NicknameValidationState
import com.mulkkam.ui.util.MutableSingleLiveData
import kotlinx.coroutines.launch

class NicknameViewModel : ViewModel() {
    private val _nicknameValidationState: MutableLiveData<NicknameValidationState> =
        MutableLiveData()
    val nicknameValidationState: MutableLiveData<NicknameValidationState>
        get() = _nicknameValidationState

    private val _onNicknameValidationError: MutableSingleLiveData<NicknameError> =
        MutableSingleLiveData()
    val onNicknameValidationError: MutableSingleLiveData<NicknameError>
        get() = _onNicknameValidationError

    fun validateNickname(nickname: String) {
        runCatching {
            Nickname(nickname)
        }.onSuccess {
            _nicknameValidationState.value = NicknameValidationState.PENDING_SERVER_VALIDATION
        }.onFailure { error ->
            _nicknameValidationState.value = NicknameValidationState.INVALID
            _onNicknameValidationError.setValue(error as NicknameError)
        }
    }

    fun checkNicknameAvailability(nickname: String) {
        viewModelScope.launch {
            runCatching {
                nicknameRepository.getNicknameValidation(nickname).getOrError()
            }.onSuccess {
                _nicknameValidationState.value = NicknameValidationState.VALID
            }.onFailure { error ->
                _nicknameValidationState.value = NicknameValidationState.INVALID
                if (error !is NicknameError) {
                    // TODO: 서버 에러일 경우 ? 닉네임 에러가 아닐 경우 ?
                    return@onFailure
                }
                _onNicknameValidationError.setValue(error)
            }
        }
    }
}
