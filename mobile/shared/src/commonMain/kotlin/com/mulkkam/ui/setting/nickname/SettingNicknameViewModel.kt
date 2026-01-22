package com.mulkkam.ui.setting.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NicknameRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingNicknameViewModel(
    private val membersRepository: MembersRepository,
    private val nicknameRepository: NicknameRepository,
    private val logger: Logger,
) : ViewModel() {
    private var newNickname: Nickname? = null

    private val _originalNicknameUiState: MutableStateFlow<MulKkamUiState<Nickname>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val originalNicknameUiState: StateFlow<MulKkamUiState<Nickname>>
        get() = _originalNicknameUiState

    private val _nicknameValidationState: MutableStateFlow<NicknameValidationUiState> =
        MutableStateFlow(NicknameValidationUiState.NONE)
    val nicknameValidationState: StateFlow<NicknameValidationUiState>
        get() = _nicknameValidationState.asStateFlow()

    private val _nicknameValidationError: MutableStateFlow<MulKkamError?> =
        MutableStateFlow(null)
    val nicknameValidationError: StateFlow<MulKkamError?>
        get() = _nicknameValidationError.asStateFlow()

    private val _onNicknameChanged: MutableSharedFlow<MulKkamUiState<Unit>> = MutableSharedFlow()
    val onNicknameChanged: SharedFlow<MulKkamUiState<Unit>>
        get() = _onNicknameChanged.asSharedFlow()

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
                _originalNicknameUiState.value =
                    MulKkamUiState.Success(Nickname(nickname))
            }.onFailure {
                _originalNicknameUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateNickname(nickname: String) {
        runCatching {
            newNickname = Nickname(nickname)
        }.onSuccess {
            when (nickname) {
                originalNicknameUiState.value.toSuccessDataOrNull()?.name -> {
                    _nicknameValidationState.value = NicknameValidationUiState.NONE
                }

                else -> {
                    _nicknameValidationState.value =
                        NicknameValidationUiState.PENDING_SERVER_VALIDATION
                }
            }
        }.onFailure { error ->
            _nicknameValidationState.value = NicknameValidationUiState.INVALID
            _nicknameValidationError.value =
                error as? MulKkamError.NicknameError ?: MulKkamError.Unknown
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

    fun saveNickname(nickname: String) {
        viewModelScope.launch {
            runCatching {
                logger.debug(LogEvent.USER_ACTION, "Saving nickname change $nickname")
                _onNicknameChanged.emit(MulKkamUiState.Loading)
                membersRepository.patchMembersNickname(nickname).getOrError()
            }.onSuccess {
                _onNicknameChanged.emit(MulKkamUiState.Success(Unit))
            }.onFailure {
                _onNicknameChanged.emit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }
    }
}
