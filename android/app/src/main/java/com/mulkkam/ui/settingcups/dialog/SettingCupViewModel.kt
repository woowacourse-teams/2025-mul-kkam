package com.mulkkam.ui.settingcups.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import kotlinx.coroutines.launch

class SettingCupViewModel : ViewModel() {
    private var _cup: MutableLiveData<CupUiModel> = MutableLiveData(EMPTY_CUP_UI_MODEL)
    val cup: LiveData<CupUiModel> get() = _cup

    private var _editType: MutableLiveData<SettingWaterCupEditType> = MutableLiveData(SettingWaterCupEditType.ADD)
    val editType: LiveData<SettingWaterCupEditType> get() = _editType

    private var _success: MutableLiveData<Boolean> = MutableLiveData(false)
    val success: LiveData<Boolean> get() = _success

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
        _cup.value = _cup.value?.copy(nickname = nickname)
    }

    fun updateAmount(amount: Int) {
        _cup.value = _cup.value?.copy(amount = amount)
    }

    fun saveCup() {
        when (editType.value) {
            SettingWaterCupEditType.ADD -> {
                addCup()
            }

            SettingWaterCupEditType.EDIT -> {
                // TODO: 수정 네트워크 추가
            }

            else -> Unit
        }
    }

    private fun addCup() {
        viewModelScope.launch {
            val result =
                cupsRepository.postCup(
                    cupAmount = _cup.value?.amount ?: 0,
                    cupNickname = _cup.value?.nickname ?: "",
                )
            runCatching {
                result.getOrError()
                _success.value = true
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun updateIntakeType(intakeType: IntakeType) {
        _cup.value = cup.value?.copy(intakeType = intakeType)
    }
}
