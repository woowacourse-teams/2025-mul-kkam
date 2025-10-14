package com.mulkkam.ui.settingbioinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.launch

class SettingBioInfoViewModel : ViewModel() {
    private val _gender = MutableLiveData<Gender?>()
    val gender: LiveData<Gender?>
        get() = _gender

    private val _weight = MutableLiveData<BioWeight?>()
    val weight: MutableLiveData<BioWeight?>
        get() = _weight

    private val _bioInfoChangeUiState = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val bioInfoChangeUiState: LiveData<MulKkamUiState<Unit>>
        get() = _bioInfoChangeUiState

    val canSave =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                value = _gender.value != null && _weight.value != null
            }

            addSource(_gender) { update() }
            addSource(_weight) { update() }
        }

    init {
        loadMemberInfo()
    }

    private fun loadMemberInfo() {
        viewModelScope.launch {
            runCatching {
                membersRepository.getMembers().getOrError()
            }.onSuccess { memberInfo ->
                _gender.value = memberInfo.gender
                _weight.value = memberInfo.weight
            }.onFailure {
                // TODO: 예외 처리
            }
        }
    }

    fun updateWeight(value: Int) {
        runCatching {
            BioWeight(value)
        }.onSuccess {
            _weight.value = it
        }.onFailure {
            _weight.value = BioWeight()
        }
    }

    fun updateGender(gender: Gender) {
        _gender.value = gender
    }

    fun saveBioInfo() {
        if (bioInfoChangeUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                mulKkamLogger.info(
                    LogEvent.USER_ACTION,
                    "Submitting bio info gender: ${gender.value}, weight: ${weight.value}",
                )
                _bioInfoChangeUiState.value = MulKkamUiState.Loading
                membersRepository
                    .postMembersPhysicalAttributes(
                        gender = gender.value ?: return@launch,
                        weight = weight.value ?: return@launch,
                    ).getOrError()
            }.onSuccess {
                _bioInfoChangeUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure {
                _bioInfoChangeUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
