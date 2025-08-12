package com.mulkkam.ui.settingnickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.di.RepositoryInjection.nicknameRepository
import com.mulkkam.domain.model.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingNicknameViewModel : ViewModel() {
    private val _nickname = MutableLiveData<Nickname?>()
    val nickname: LiveData<Nickname?>
        get() = _nickname

    private val _nicknameValidationState: MutableLiveData<NicknameValidationUiState> =
        MutableLiveData()
    val nicknameValidationState: MutableLiveData<NicknameValidationUiState>
        get() = _nicknameValidationState

    private val _onNicknameValidationError: MutableSingleLiveData<MulKkamError> =
        MutableSingleLiveData()
    val onNicknameValidationError: MutableSingleLiveData<MulKkamError>
        get() = _onNicknameValidationError

    private val _onNicknameChanged = MutableSingleLiveData<Unit>()
    val onNicknameChanged: SingleLiveData<Unit>
        get() = _onNicknameChanged

    init {
        viewModelScope.launch {
            runCatching {
                RepositoryInjection.membersRepository.getMembersNickname().getOrError()
            }.onSuccess { nickname ->
                _nickname.value = Nickname(nickname)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

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

    fun saveNickname(nickname: String) {
        viewModelScope.launch {
            runCatching {
                RepositoryInjection.membersRepository.patchMembersNickname(nickname).getOrError()
            }.onSuccess {
                _onNicknameChanged.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
