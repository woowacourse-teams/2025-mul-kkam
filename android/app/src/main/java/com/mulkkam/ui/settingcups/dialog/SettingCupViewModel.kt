package com.mulkkam.ui.settingcups.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingCupViewModel : ViewModel() {
    private var _cup: MutableLiveData<CupUiModel> = MutableLiveData(EMPTY_CUP_UI_MODEL)
    val cup: LiveData<CupUiModel> get() = _cup

    private var _editType: MutableLiveData<SettingWaterCupEditType> = MutableLiveData(SettingWaterCupEditType.ADD)
    val editType: LiveData<SettingWaterCupEditType> get() = _editType

    private var _saveSuccess: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val saveSuccess: SingleLiveData<Unit> get() = _saveSuccess

    private var _deleteSuccess: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val deleteSuccess: SingleLiveData<Unit> get() = _deleteSuccess

    fun initCup(cup: CupUiModel?) {
        if (cup == null) {
            _editType.value = SettingWaterCupEditType.ADD
            _cup.value = EMPTY_CUP_UI_MODEL
        } else {
            _editType.value = SettingWaterCupEditType.EDIT
            cup.let { _cup.value = it }
        }
    }

    fun updateNickname(nickname: String) {
        _cup.value = cup.value?.copy(nickname = nickname)
    }

    fun updateAmount(amount: Int) {
        _cup.value = cup.value?.copy(amount = amount)
    }

    fun updateIntakeType(intakeType: IntakeType) {
        _cup.value = cup.value?.copy(intakeType = intakeType)
    }

    fun updateEmoji(emoji: String) {
        _cup.value = cup.value?.copy(emoji = emoji)
    }

    fun saveCup() {
        when (editType.value) {
            SettingWaterCupEditType.ADD -> {
                addCup()
            }

            SettingWaterCupEditType.EDIT -> {
                editCup()
            }

            else -> Unit
        }
    }

    private fun addCup() {
        viewModelScope.launch {
            val result =
                cupsRepository.postCup(
                    cup = cup.value?.toDomain() ?: return@launch,
                )
            runCatching {
                result.getOrError()
            }.onSuccess {
                _saveSuccess.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    private fun editCup() {
        viewModelScope.launch {
            runCatching {
                cupsRepository
                    .patchCup(
                        cup = cup.value?.toDomain() ?: return@launch,
                    ).getOrError()
            }.onSuccess {
                _saveSuccess.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun deleteCup() {
        viewModelScope.launch {
            runCatching {
                cupsRepository
                    .deleteCup(
                        id = cup.value?.id ?: return@launch,
                    ).getOrError()
            }.onSuccess {
                _deleteSuccess.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
