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
import com.mulkkam.ui.model.MulKkamUiState.Loading.toSuccessDataOrNull
import com.mulkkam.ui.settingcups.model.CupEmojisUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.settingcups.model.toUi
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingCupViewModel : ViewModel() {
    private val _cup: MutableLiveData<CupUiModel> = MutableLiveData(EMPTY_CUP_UI_MODEL)
    val cup: LiveData<CupUiModel> get() = _cup

    private val _editType: MutableLiveData<SettingWaterCupEditType> = MutableLiveData(SettingWaterCupEditType.ADD)
    val editType: LiveData<SettingWaterCupEditType> get() = _editType

    private val _cupNameValidity: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val cupNameValidity: LiveData<MulKkamUiState<Unit>> get() = _cupNameValidity

    private val _amountValidity: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val amountValidity: LiveData<MulKkamUiState<Unit>> get() = _amountValidity

    private val _cupEmojisUiState: MutableLiveData<MulKkamUiState<CupEmojisUiModel>> =
        MutableLiveData<MulKkamUiState<CupEmojisUiModel>>(MulKkamUiState.Idle)
    val cupEmojisUiState: LiveData<MulKkamUiState<CupEmojisUiModel>> get() = _cupEmojisUiState

    private var originalCup: CupUiModel = EMPTY_CUP_UI_MODEL

    private val hasChanges: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                value = cup.value != originalCup || cupEmojisUiState.value?.toSuccessDataOrNull()?.selectedCupEmoji != null
            }
            addSource(_cupEmojisUiState) { update() }
            addSource(_cup) { update() }
        }

    val isSaveAvailable: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                val hasChanged = hasChanges.value == true
                if (!hasChanged) {
                    value = false
                    return
                }

                val isNameAvailable = _cupNameValidity.value is MulKkamUiState.Success
                val isAmountAvailable = _amountValidity.value is MulKkamUiState.Success
                val isEmojiSelected = cupEmojisUiState.value?.toSuccessDataOrNull()?.selectedCupEmoji != null
                val isEmojiChanged =
                    cupEmojisUiState.value
                        ?.toSuccessDataOrNull()
                        ?.selectedCupEmoji
                        ?.id != originalCup.emoji.id
                val isIntakeTypeChanged = cup.value?.intakeType != originalCup.intakeType

                value =
                    when (editType.value) {
                        SettingWaterCupEditType.ADD -> {
                            isNameAvailable && isAmountAvailable && isEmojiSelected
                        }
                        else -> {
                            isNameAvailable || isAmountAvailable || isEmojiChanged || isIntakeTypeChanged
                        }
                    }
            }

            addSource(_cup) { update() }
            addSource(_cupNameValidity) { update() }
            addSource(_amountValidity) { update() }
            addSource(_cupEmojisUiState) { update() }
            addSource(hasChanges) { update() }
        }

    private val _saveSuccess = MutableSingleLiveData<Unit>()
    val saveSuccess: SingleLiveData<Unit> get() = _saveSuccess

    private val _deleteSuccess = MutableSingleLiveData<Unit>()
    val deleteSuccess: SingleLiveData<Unit> get() = _deleteSuccess

    init {
        loadCupEmojis()
    }

    private fun loadCupEmojis() {
        if (cupEmojisUiState.value is MulKkamUiState.Success) return

        viewModelScope.launch {
            runCatching {
                _cupEmojisUiState.value = MulKkamUiState.Loading
                cupsRepository.getCupEmojis().getOrError()
            }.onSuccess {
                _cupEmojisUiState.value = MulKkamUiState.Success(it.toUi())
                when (editType.value) {
                    SettingWaterCupEditType.ADD -> selectEmoji(it.firstOrNull()?.id ?: return@onSuccess)
                    SettingWaterCupEditType.EDIT -> selectEmoji(cup.value?.emoji?.id ?: return@onSuccess)
                    null -> return@onSuccess
                }
            }
        }
    }

    fun initCup(cup: CupUiModel?) {
        originalCup = cup ?: EMPTY_CUP_UI_MODEL
        _cup.value = originalCup
        _editType.value =
            when (cup) {
                null -> SettingWaterCupEditType.ADD
                else -> SettingWaterCupEditType.EDIT
            }
        _cupNameValidity.value = MulKkamUiState.Idle
        _amountValidity.value = MulKkamUiState.Idle
    }

    fun updateCupName(name: String) {
        val updated = cup.value?.copy(name = name) ?: return
        _cup.value = updated

        if (name == originalCup.name) {
            _cupNameValidity.value = MulKkamUiState.Idle
            return
        }

        runCatching {
            CupName(name)
        }.onSuccess {
            _cupNameValidity.value = MulKkamUiState.Success(Unit)
        }.onFailure {
            _cupNameValidity.value = MulKkamUiState.Failure(it.toMulKkamError())
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
            val cup = cup.value?.toDomain() ?: return@launch

            runCatching {
                cupsRepository
                    .postCup(
                        name = cup.name,
                        amount = cup.amount,
                        intakeType = cup.intakeType,
                        emojiId =
                            cupEmojisUiState.value
                                ?.toSuccessDataOrNull()
                                ?.selectedCupEmoji
                                ?.id ?: return@launch,
                    ).getOrError()
            }.onSuccess {
                _saveSuccess.setValue(Unit)
            }
        }
    }

    private fun editCup() {
        viewModelScope.launch {
            val cup = cup.value?.toDomain() ?: return@launch

            runCatching {
                cupsRepository
                    .patchCup(
                        id = cup.id,
                        name = cup.name,
                        amount = cup.amount,
                        intakeType = cup.intakeType,
                        emojiId =
                            cupEmojisUiState.value
                                ?.toSuccessDataOrNull()
                                ?.selectedCupEmoji
                                ?.id ?: return@launch,
                    ).getOrError()
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

    fun selectEmoji(emojiId: Long) {
        val emoji = cupEmojisUiState.value?.toSuccessDataOrNull() ?: return
        _cupEmojisUiState.value = MulKkamUiState.Success<CupEmojisUiModel>(emoji.selectCupEmoji(emojiId))
    }
}
