package com.mulkkam.ui.settingcups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.settingcups.model.toUi
import kotlinx.coroutines.launch

class SettingCupsViewModel : ViewModel() {
    private var _cupsUiState: MutableLiveData<MulKkamUiState<CupsUiModel>> = MutableLiveData(MulKkamUiState.Idle)
    val cupsUiState: LiveData<MulKkamUiState<CupsUiModel>> get() = _cupsUiState

    init {
        loadCups()
    }

    fun loadCups() {
        if (cupsUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _cupsUiState.value = MulKkamUiState.Loading
                cupsRepository.getCups().getOrError()
            }.onSuccess { cups ->
                _cupsUiState.value = MulKkamUiState.Success<CupsUiModel>(cups.toUi())
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
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
                    _cupsUiState.value = MulKkamUiState.Success<CupsUiModel>(cups.toUi())
                }
            }.onFailure {
                _cupsUiState.value = cupsUiState.value
            }
        }
    }
}
