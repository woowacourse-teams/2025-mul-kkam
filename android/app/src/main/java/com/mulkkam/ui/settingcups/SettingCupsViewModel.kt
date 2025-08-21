package com.mulkkam.ui.settingcups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.settingcups.model.toUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingCupsViewModel : ViewModel() {
    private var _cupsUiState: MutableLiveData<MulKkamUiState<CupsUiModel>> = MutableLiveData(MulKkamUiState.Idle)
    val cupsUiState: LiveData<MulKkamUiState<CupsUiModel>> get() = _cupsUiState

    private var _cupsReorderUiState: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData(MulKkamUiState.Idle)
    val cupsReorderUiState: LiveData<MulKkamUiState<Unit>> get() = _cupsReorderUiState

    private var _cupsResetUiState: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData(MulKkamUiState.Idle)
    val cupsResetUiState: LiveData<MulKkamUiState<Unit>> get() = _cupsResetUiState

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
        val currentOrder = cupsUiState.value?.toSuccessDataOrNull()?.cups ?: return
        val isReordering = cupsReorderUiState.value is MulKkamUiState.Loading

        if (newOrder == currentOrder || isReordering) {
            _cupsUiState.value = cupsUiState.value
            return
        }

        val reorderedCups = Cups(newOrder.map { it.toDomain() }).reorderRanks()

        viewModelScope.launch {
            _cupsReorderUiState.value = MulKkamUiState.Loading
            runCatching {
                cupsRepository.putCupsRank(reorderedCups).getOrError()
            }.onSuccess { cups ->
                if (reorderedCups != cups) {
                    _cupsUiState.value = MulKkamUiState.Success(cups.toUi())
                }
                delay(REORDER_DELAY_TIME)
                _cupsReorderUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure { t ->
                _cupsUiState.value = cupsUiState.value
                _cupsReorderUiState.value = MulKkamUiState.Failure(t.toMulKkamError())
            }
        }
    }

    fun resetCups() {
        viewModelScope.launch {
            _cupsResetUiState.value = MulKkamUiState.Loading
            runCatching {
                cupsRepository.resetCups().getOrError()
            }.onSuccess {
                _cupsResetUiState.value = MulKkamUiState.Success(Unit)
                loadCups()
            }.onFailure {
                _cupsResetUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    companion object {
        private const val REORDER_DELAY_TIME: Long = 2000L
    }
}
