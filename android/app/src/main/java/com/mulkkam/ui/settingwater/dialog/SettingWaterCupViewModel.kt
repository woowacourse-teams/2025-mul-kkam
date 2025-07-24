package com.mulkkam.ui.settingwater.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.ui.settingwater.model.CupUiModel
import com.mulkkam.ui.settingwater.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingwater.model.SettingWaterCupEditType

class SettingWaterCupViewModel : ViewModel() {
    private var _cup: MutableLiveData<CupUiModel> = MutableLiveData(EMPTY_CUP_UI_MODEL)
    val cup: LiveData<CupUiModel> get() = _cup

    private var _editType: MutableLiveData<SettingWaterCupEditType> = MutableLiveData(SettingWaterCupEditType.ADD)
    val editType: LiveData<SettingWaterCupEditType> get() = _editType

    fun initCup(cup: CupUiModel?) {
        if (cup == null) {
            _editType.value = SettingWaterCupEditType.ADD
            _cup.value = EMPTY_CUP_UI_MODEL
        } else {
            _editType.value = SettingWaterCupEditType.EDIT
            cup.let { _cup.value = it }
        }
    }
}
