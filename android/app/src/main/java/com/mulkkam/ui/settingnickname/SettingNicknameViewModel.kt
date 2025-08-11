package com.mulkkam.ui.settingnickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.di.RepositoryInjection.nicknameRepository
import com.mulkkam.domain.model.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.NicknameValidationState
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingNicknameViewModel : ViewModel() {
    private val _currentNickname = MutableLiveData<Nickname?>()
    val currentNickname: LiveData<Nickname?>
        get() = _currentNickname

    private val _nicknameValidationState: MutableLiveData<NicknameValidationState> =
        MutableLiveData()
    val nicknameValidationState: MutableLiveData<NicknameValidationState>
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
                _currentNickname.value = Nickname(nickname)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

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
