package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.nicknameRepository
import com.mulkkam.domain.model.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.NicknameValidationState
import com.mulkkam.ui.util.MutableSingleLiveData
import kotlinx.coroutines.launch

class NicknameViewModel : ViewModel() {
    private val _nicknameValidationState: MutableLiveData<NicknameValidationState> =
        MutableLiveData()
    val nicknameValidationState: MutableLiveData<NicknameValidationState>
        get() = _nicknameValidationState

    private val _onNicknameValidationError: MutableSingleLiveData<MulKkamError> =
        MutableSingleLiveData()
    val onNicknameValidationError: MutableSingleLiveData<MulKkamError>
        get() = _onNicknameValidationError

    fun validateNickname(nickname: String) {
        runCatching {
            Nickname(nickname)
        }.onSuccess {
            _nicknameValidationState.value = NicknameValidationState.PENDING_SERVER_VALIDATION
        }.onFailure { error ->
            _nicknameValidationState.value = NicknameValidationState.INVALID
            _onNicknameValidationError.setValue(error.toMulKkamError())
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
                _onNicknameValidationError.setValue(error.toMulKkamError())
            }
        }
    }
}
