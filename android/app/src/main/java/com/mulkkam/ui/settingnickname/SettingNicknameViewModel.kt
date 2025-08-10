package com.mulkkam.ui.settingnickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.di.RepositoryInjection.nicknameRepository
import com.mulkkam.domain.model.MulKkamError.NicknameError
import com.mulkkam.domain.model.Nickname
import com.mulkkam.ui.model.NicknameValidationState
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingNicknameViewModel : ViewModel() {
    private val _currentNickname = MutableLiveData<String?>()
    val currentNickname: LiveData<String?>
        get() = _currentNickname

    private val _nicknameValidationState: MutableLiveData<NicknameValidationState> = MutableLiveData()
    val nicknameValidationState: MutableLiveData<NicknameValidationState>
        get() = _nicknameValidationState

    private val _onNicknameValidationError: MutableSingleLiveData<NicknameError> = MutableSingleLiveData()
    val onNicknameValidationError: MutableSingleLiveData<NicknameError>
        get() = _onNicknameValidationError

    private val _onNicknameChanged = MutableSingleLiveData<Unit>()
    val onNicknameChanged: SingleLiveData<Unit>
        get() = _onNicknameChanged

    init {
        viewModelScope.launch {
            val result = RepositoryInjection.membersRepository.getMembersNickname()
            runCatching {
                _currentNickname.value = result.getOrError()
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

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

    fun saveNickname(nickname: String) {
        viewModelScope.launch {
            val result = RepositoryInjection.membersRepository.patchMembersNickname(nickname)
            runCatching {
                result.getOrError()
                _onNicknameChanged.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
