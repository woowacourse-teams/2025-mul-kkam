package com.mulkkam.ui.settingnickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingNicknameViewModel : ViewModel() {
    private val _currentNickname = MutableLiveData<String?>()
    val currentNickname: LiveData<String?>
        get() = _currentNickname

    private val _isValidNickname = MutableLiveData<Boolean?>()
    val isValidNickname: MutableLiveData<Boolean?>
        get() = _isValidNickname

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

    fun checkNicknameDuplicate(nickname: String) {
        viewModelScope.launch {
            val result =
                RepositoryInjection.membersRepository.getMembersNicknameValidation(nickname)
            runCatching {
                result.getOrError()
                _isValidNickname.value = true
            }.onFailure {
                _isValidNickname.value = false
            }
        }
    }

    fun clearNicknameValidationState() {
        _isValidNickname.value = null
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
