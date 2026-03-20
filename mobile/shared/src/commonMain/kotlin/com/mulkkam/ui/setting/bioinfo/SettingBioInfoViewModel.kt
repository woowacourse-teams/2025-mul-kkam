package com.mulkkam.ui.setting.bioinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingBioInfoViewModel(
    private val membersRepository: MembersRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _gender: MutableStateFlow<Gender?> = MutableStateFlow(null)
    val gender: StateFlow<Gender?>
        get() = _gender.asStateFlow()

    private val _weight: MutableStateFlow<BioWeight?> = MutableStateFlow(null)
    val weight: StateFlow<BioWeight?>
        get() = _weight.asStateFlow()

    private val _onBioInfoChanged: MutableSharedFlow<MulKkamUiState<Unit>> = MutableSharedFlow()
    val onBioInfoChanged: SharedFlow<MulKkamUiState<Unit>>
        get() = _onBioInfoChanged.asSharedFlow()

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
        viewModelScope.launch {
            runCatching {
                logger.info(
                    LogEvent.USER_ACTION,
                    "Submitting bio info gender: ${gender.value}, weight: ${weight.value}",
                )
                _onBioInfoChanged.emit(MulKkamUiState.Loading)
                membersRepository
                    .postMembersPhysicalAttributes(
                        gender = gender.value ?: return@launch,
                        weight = weight.value ?: return@launch,
                    ).getOrError()
            }.onSuccess {
                _onBioInfoChanged.emit(MulKkamUiState.Success(Unit))
            }.onFailure {
                _onBioInfoChanged.emit(MulKkamUiState.Failure(it.toMulKkamError()))
            }
        }
    }
}
