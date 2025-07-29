package com.mulkkam.ui.settingwater

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.ui.settingwater.model.CupsUiModel
import com.mulkkam.ui.settingwater.model.toUi
import kotlinx.coroutines.launch

class SettingWaterViewModel : ViewModel() {
    private var _cups: MutableLiveData<CupsUiModel> = MutableLiveData()
    val cups: LiveData<CupsUiModel> get() = _cups

    fun loadCups() {
        viewModelScope.launch {
            runCatching {
                cupsRepository.getCups()
            }.onSuccess {
                _cups.value = it.toUi()
            }
        }
    }
}
