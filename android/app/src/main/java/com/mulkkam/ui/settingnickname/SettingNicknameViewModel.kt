package com.mulkkam.ui.settingnickname

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingNicknameViewModel : ViewModel() {
    private val _nicknameValidationState = MutableLiveData<Boolean?>()
    val nicknameValidationState: MutableLiveData<Boolean?>
        get() = _nicknameValidationState

    private val _onNicknameChanged = MutableSingleLiveData<Unit>()
    val onNicknameChanged: SingleLiveData<Unit>
        get() = _onNicknameChanged

    fun checkNicknameDuplicate(nickname: String) {
        viewModelScope.launch {
            val result = RepositoryInjection.membersRepository.getMembersNicknameValidation(nickname)
            runCatching {
                result.getOrError()
                _nicknameValidationState.value = true
            }.onFailure {
                _nicknameValidationState.value = false
            }
        }
    }

    fun clearNicknameValidationState() {
        _nicknameValidationState.value = null
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
