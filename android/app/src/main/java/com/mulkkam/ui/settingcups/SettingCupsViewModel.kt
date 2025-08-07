package com.mulkkam.ui.settingcups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.Cups
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.settingcups.model.toUi
import kotlinx.coroutines.launch

class SettingCupsViewModel : ViewModel() {
    private var _cups: MutableLiveData<CupsUiModel> = MutableLiveData()
    val cups: LiveData<CupsUiModel> get() = _cups

    init {
        loadCups()
    }

    fun loadCups() {
        viewModelScope.launch {
            runCatching {
                cupsRepository.getCups().getOrError()
            }.onSuccess { cups ->
                _cups.value = cups.toUi()
            }.onFailure {
                // TODO: 예외 처리
            }
        }
    }

    fun updateCupOrder(newOrder: List<CupUiModel>) {
        val reorderedCups =
            Cups(
                cups = newOrder.map { it.toDomain() },
            ).reorderRanks()

        viewModelScope.launch {
            runCatching {
                cupsRepository.putCupsRank(reorderedCups).getOrError()
            }.onSuccess { cups ->
                if (reorderedCups != cups) {
                    _cups.value = cups.toUi()
                }
            }.onFailure {
                _cups.value = cups.value
                // TODO: 예외 처리
            }
        }
    }
}
