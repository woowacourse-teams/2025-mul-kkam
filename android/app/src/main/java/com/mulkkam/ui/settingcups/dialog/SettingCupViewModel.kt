package com.mulkkam.ui.settingcups.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingCupViewModel : ViewModel() {
    private val _cup: MutableLiveData<CupUiModel> = MutableLiveData(EMPTY_CUP_UI_MODEL)
    val cup: LiveData<CupUiModel> get() = _cup

    private val _editType: MutableLiveData<SettingWaterCupEditType> = MutableLiveData(SettingWaterCupEditType.ADD)
    val editType: LiveData<SettingWaterCupEditType> get() = _editType

    private val _nicknameValidity: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val nicknameValidity: LiveData<MulKkamUiState<Unit>> get() = _nicknameValidity

    private val _amountValidity: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val amountValidity: LiveData<MulKkamUiState<Unit>> get() = _amountValidity

    private var originalCup: CupUiModel = EMPTY_CUP_UI_MODEL

    private val hasChanges: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                value = _cup.value != originalCup
            }
            addSource(_cup) { update() }
        }

    val isSaveAvailable: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                val current =
                    _cup.value ?: run {
                        value = false
                        return
                    }
                val hasChanged = hasChanges.value == true
                if (!hasChanged) {
                    value = false
                    return
                }

                val nicknameChanged = current.nickname != originalCup.nickname
                val amountChanged = current.amount != originalCup.amount

                val isNicknameAvailable =
                    if (nicknameChanged) _nicknameValidity.value is MulKkamUiState.Success else true
                val isAmountAvailable =
                    if (amountChanged) _amountValidity.value is MulKkamUiState.Success else true

                value = isNicknameAvailable && isAmountAvailable
            }

            addSource(_cup) { update() }
            addSource(_nicknameValidity) { update() }
            addSource(_amountValidity) { update() }
            addSource(hasChanges) { update() }
        }

    private val _saveSuccess = MutableSingleLiveData<Unit>()
    val saveSuccess: SingleLiveData<Unit> get() = _saveSuccess

    private val _deleteSuccess = MutableSingleLiveData<Unit>()
    val deleteSuccess: SingleLiveData<Unit> get() = _deleteSuccess

    fun initCup(cup: CupUiModel?) {
        originalCup = cup ?: EMPTY_CUP_UI_MODEL
        _cup.value = originalCup
        _editType.value =
            when (cup) {
                null -> SettingWaterCupEditType.ADD
                else -> SettingWaterCupEditType.EDIT
            }
        _nicknameValidity.value = MulKkamUiState.Idle
        _amountValidity.value = MulKkamUiState.Idle
    }

    fun updateNickname(nickname: String) {
        val updated = cup.value?.copy(nickname = nickname) ?: return
        _cup.value = updated

        if (nickname == originalCup.nickname) {
            _nicknameValidity.value = MulKkamUiState.Idle
            return
        }

        runCatching {
            CupName(nickname)
        }.onSuccess {
            _nicknameValidity.value = MulKkamUiState.Success(Unit)
        }.onFailure {
            _nicknameValidity.value = MulKkamUiState.Failure(it.toMulKkamError())
        }
    }

    fun updateAmount(amount: Int) {
        val updated = cup.value?.copy(amount = amount) ?: return
        _cup.value = updated

        if (amount == originalCup.amount) {
            _amountValidity.value = MulKkamUiState.Idle
            return
        }

        runCatching {
            CupAmount(amount)
        }.onSuccess {
            _amountValidity.value = MulKkamUiState.Success(Unit)
        }.onFailure {
            _amountValidity.value = MulKkamUiState.Failure(it.toMulKkamError())
        }
    }

    fun updateIntakeType(intakeType: IntakeType) {
        _cup.value = _cup.value?.copy(intakeType = intakeType)
    }

    fun updateEmoji(emoji: String) {
        _cup.value = _cup.value?.copy(emoji = emoji)
    }

    fun saveCup() {
        if (isSaveAvailable.value != true) return
        when (_editType.value) {
            SettingWaterCupEditType.ADD -> addCup()
            SettingWaterCupEditType.EDIT -> editCup()
            else -> Unit
        }
    }

    private fun addCup() {
        viewModelScope.launch {
            val cupUiModel = cup.value ?: return@launch

            runCatching {
                cupsRepository.postCup(cupUiModel.toDomain()).getOrError()
            }.onSuccess {
                _saveSuccess.setValue(Unit)
            }
        }
    }

    private fun editCup() {
        viewModelScope.launch {
            val cupUiModel = cup.value ?: return@launch

            runCatching {
                cupsRepository.patchCup(cupUiModel.toDomain()).getOrError()
            }.onSuccess {
                _saveSuccess.setValue(Unit)
            }
        }
    }

    fun deleteCup() {
        viewModelScope.launch {
            val cupId = cup.value?.id ?: return@launch

            runCatching {
                cupsRepository.deleteCup(cupId).getOrError()
            }.onSuccess {
                _deleteSuccess.setValue(Unit)
            }
        }
    }
}
