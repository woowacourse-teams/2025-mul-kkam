package com.mulkkam.ui.settingcups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.settingcups.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingCupsViewModel(
    private val cupsRepository: CupsRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _cupsUiState: MutableStateFlow<MulKkamUiState<CupsUiModel>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val cupsUiState: StateFlow<MulKkamUiState<CupsUiModel>> get() = _cupsUiState.asStateFlow()

    private val _cupsReorderUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val cupsReorderUiState: StateFlow<MulKkamUiState<Unit>>
        get() = _cupsReorderUiState.asStateFlow()

    private val _cupsResetUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val cupsResetUiState: StateFlow<MulKkamUiState<Unit>> get() = _cupsResetUiState.asStateFlow()

    private var previousCupsUiModel: CupsUiModel? = null

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
                val cupsUiModel: CupsUiModel = cups.toUi()
                _cupsUiState.value = MulKkamUiState.Success<CupsUiModel>(cupsUiModel)
                previousCupsUiModel = cupsUiModel
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateCupOrder(newOrder: List<CupUiModel>) {
        val currentCups: CupsUiModel = cupsUiState.value.toSuccessDataOrNull() ?: return
        val isReordering: Boolean = cupsReorderUiState.value is MulKkamUiState.Loading
        if (isReordering) return

        val reorderedCups: Cups = Cups(newOrder.map { it.toDomain() }).reorderRanks()
        val reorderedUiModel: CupsUiModel = reorderedCups.toUi()
        val newOrderIds: List<Long> = reorderedUiModel.cups.map { cup -> cup.id }
        val lastSyncedIds: List<Long>? = previousCupsUiModel?.cups?.map { cup -> cup.id }

        if (newOrderIds == lastSyncedIds) {
            _cupsUiState.value = MulKkamUiState.Success(reorderedUiModel)
            return
        }

        viewModelScope.launch {
            logger.info(LogEvent.USER_ACTION, "Saving cup reorder from settings")
            _cupsReorderUiState.value = MulKkamUiState.Loading
            runCatching {
                cupsRepository.putCupsRank(reorderedCups).getOrError()
            }.onSuccess { cups ->
                val updatedUiModel: CupsUiModel = cups.toUi()
                previousCupsUiModel = updatedUiModel
                _cupsUiState.value = MulKkamUiState.Success(updatedUiModel)
                _cupsReorderUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure {
                val fallbackUiModel: CupsUiModel = previousCupsUiModel ?: currentCups
                _cupsUiState.value = MulKkamUiState.Success(fallbackUiModel)
                _cupsReorderUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun resetCups() {
        viewModelScope.launch {
            logger.info(LogEvent.USER_ACTION, "Resetting cups to default")
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

    fun applyOptimisticCupOrder(newOrder: List<CupUiModel>) {
        val reorderedCups = Cups(newOrder.map { it.toDomain() }).reorderRanks()
        _cupsUiState.value = MulKkamUiState.Success(reorderedCups.toUi())
    }
}
