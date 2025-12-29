package com.mulkkam.ui.onboarding.nickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.domain.repository.NicknameRepository
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.util.MutableSingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel
    @Inject
    constructor(
        private val nicknameRepository: NicknameRepository,
    ) : ViewModel() {
        private val _nickname: MutableLiveData<Nickname> = MutableLiveData()
        val nickname: MutableLiveData<Nickname>
            get() = _nickname

        private val _nicknameValidationState: MutableLiveData<NicknameValidationUiState> =
            MutableLiveData()
        val nicknameValidationState: MutableLiveData<NicknameValidationUiState>
            get() = _nicknameValidationState

        private val _onNicknameValidationError: MutableSingleLiveData<MulKkamError> =
            MutableSingleLiveData()
        val onNicknameValidationError: MutableSingleLiveData<MulKkamError>
            get() = _onNicknameValidationError

        fun updateNickname(nickname: String) {
            runCatching {
                _nickname.value = Nickname(nickname)
            }.onSuccess {
                _nicknameValidationState.value = NicknameValidationUiState.PENDING_SERVER_VALIDATION
            }.onFailure { error ->
                _nicknameValidationState.value = NicknameValidationUiState.INVALID
                _onNicknameValidationError.setValue(error as? NicknameError ?: MulKkamError.Unknown)
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
                        _onNicknameValidationError.setValue(MulKkamError.NetworkUnavailable)
                        return@onFailure
                    }
                    _onNicknameValidationError.setValue(error)
                }
            }
        }
    }
