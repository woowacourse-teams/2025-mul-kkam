package com.mulkkam.ui.settingnickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.di.RepositoryInjection.nicknameRepository
import com.mulkkam.domain.model.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.util.MutableSingleLiveData
import kotlinx.coroutines.launch

class SettingNicknameViewModel : ViewModel() {
    private val _newNickname: MutableLiveData<Nickname> = MutableLiveData<Nickname>()
    val newNickname: LiveData<Nickname>
        get() = _newNickname

    private val _originalNicknameUiState: MutableLiveData<MulKkamUiState<Nickname>> = MutableLiveData(MulKkamUiState.Idle)
    val originalNicknameUiState: LiveData<MulKkamUiState<Nickname>>
        get() = _originalNicknameUiState

    private val _nicknameValidationState: MutableLiveData<NicknameValidationUiState> =
        MutableLiveData()
    val nicknameValidationState: MutableLiveData<NicknameValidationUiState>
        get() = _nicknameValidationState

    private val _onNicknameValidationError: MutableSingleLiveData<MulKkamError> =
        MutableSingleLiveData()
    val onNicknameValidationError: MutableSingleLiveData<MulKkamError>
        get() = _onNicknameValidationError

    private val _nicknameChangeUiState: MutableLiveData<MulKkamUiState<Unit>> =
        MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val nicknameChangeUiState: LiveData<MulKkamUiState<Unit>>
        get() = _nicknameChangeUiState

    init {
        loadOriginalNickname()
    }

    private fun loadOriginalNickname() {
        if (originalNicknameUiState.value is MulKkamUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                _originalNicknameUiState.value = MulKkamUiState.Loading
                membersRepository.getMembersNickname().getOrError()
            }.onSuccess { nickname ->
                _originalNicknameUiState.value = MulKkamUiState.Success<Nickname>(Nickname(nickname))
            }.onFailure {
                _originalNicknameUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateNickname(nickname: String) {
        runCatching {
            _newNickname.value = Nickname(nickname)
        }.onSuccess {
            when (nickname) {
                originalNicknameUiState.value?.toSuccessDataOrNull()?.name -> {
                    _nicknameValidationState.value = NicknameValidationUiState.SAME_AS_BEFORE
                }

                else -> {
                    _nicknameValidationState.value = NicknameValidationUiState.PENDING_SERVER_VALIDATION
                }
            }
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
        if (nicknameChangeUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _nicknameChangeUiState.value = MulKkamUiState.Loading
                membersRepository.patchMembersNickname(nickname).getOrError()
            }.onSuccess {
                _nicknameChangeUiState.value = MulKkamUiState.Success<Unit>(Unit)
            }.onFailure {
                _nicknameChangeUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
