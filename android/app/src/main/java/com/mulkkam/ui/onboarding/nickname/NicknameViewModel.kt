package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.domain.repository.NicknameRepository
import com.mulkkam.ui.model.NicknameValidationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel
    @Inject
    constructor(
        private val nicknameRepository: NicknameRepository,
    ) : ViewModel() {
        private var newNickname: Nickname? = null

        private val _nicknameValidationState: MutableStateFlow<NicknameValidationUiState> =
            MutableStateFlow(NicknameValidationUiState.NONE)
        val nicknameValidationState: StateFlow<NicknameValidationUiState>
            get() = _nicknameValidationState.asStateFlow()

        private val _onNicknameValidationError: MutableStateFlow<MulKkamError?> =
            MutableStateFlow(null)
        val onNicknameValidationError: StateFlow<MulKkamError?>
            get() = _onNicknameValidationError.asStateFlow()

        fun updateNickname(nickname: String) {
            runCatching {
                newNickname = Nickname(nickname)
            }.onSuccess {
                _nicknameValidationState.value = NicknameValidationUiState.PENDING_SERVER_VALIDATION
            }.onFailure { error ->
                _nicknameValidationState.value = NicknameValidationUiState.INVALID
                _onNicknameValidationError.value = error as? NicknameError ?: MulKkamError.Unknown
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
                        _onNicknameValidationError.value = MulKkamError.NetworkUnavailable
                        return@onFailure
                    }
                    _onNicknameValidationError.value = error
                }
            }
        }
    }
