package com.mulkkam.ui.settingcups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.settingcups.model.toUi
import kotlinx.coroutines.launch

class SettingCupsViewModel : ViewModel() {
    private var _cups: MutableLiveData<CupsUiModel> = MutableLiveData()
    val cups: LiveData<CupsUiModel> get() = _cups

    init {
        loadCups()
    }

    private fun loadCups() {
        viewModelScope.launch {
            val result = cupsRepository.getCups()
            runCatching {
                _cups.value = result.getOrError().toUi()
            }.onFailure {
                // TODO: 예외 처리
            }
        }
    }
}
