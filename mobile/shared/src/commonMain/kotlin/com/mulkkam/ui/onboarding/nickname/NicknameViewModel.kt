package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.domain.repository.NicknameRepository
import com.mulkkam.ui.model.NicknameValidationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NicknameViewModel(
    private val nicknameRepository: NicknameRepository,
) : ViewModel() {
    private val _nicknameValidationState: MutableStateFlow<NicknameValidationUiState> =
        MutableStateFlow(NicknameValidationUiState.NONE)
    val nicknameValidationState: StateFlow<NicknameValidationUiState>
        get() = _nicknameValidationState.asStateFlow()

    private val _nicknameValidationError: MutableStateFlow<MulKkamError?> =
        MutableStateFlow(null)
    val nicknameValidationError: StateFlow<MulKkamError?>
        get() = _nicknameValidationError.asStateFlow()

    fun updateNickname(nickname: String) {
        runCatching {
            Nickname(nickname)
        }.onSuccess {
            _nicknameValidationState.value = NicknameValidationUiState.PENDING_SERVER_VALIDATION
        }.onFailure { error ->
            _nicknameValidationState.value = NicknameValidationUiState.INVALID
            _nicknameValidationError.value = error as? NicknameError ?: MulKkamError.Unknown
        }
    }

    fun checkNicknameAvailability(nickname: String) {
        viewModelScope.launch {
            runCatching {
                nicknameRepository.getNicknameValidation(nickname).getOrError()
            }.onSuccess {
                _nicknameValidationState.value = NicknameValidationUiState.VALID
            }.onFailure { error ->
                _nicknameValidationState.value = NicknameValidationUiState.INVALID
                if (error !is NicknameError) {
                    _nicknameValidationError.value = MulKkamError.NetworkUnavailable
                    return@onFailure
                }
                _nicknameValidationError.value = error
            }
        }
    }

    fun initNicknameValidation(nicknameValidationUiState: NicknameValidationUiState) {
        _nicknameValidationState.value = nicknameValidationUiState
    }
}
