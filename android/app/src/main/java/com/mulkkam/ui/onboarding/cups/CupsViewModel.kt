package com.mulkkam.ui.onboarding.cups

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
import kotlinx.coroutines.launch

class CupsViewModel : ViewModel() {
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
                cupsRepository.getCupsDefault().getOrError()
            }.onSuccess { cups ->
                _cupsUiState.value = MulKkamUiState.Success<CupsUiModel>(cups.toUi())
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateCupOrder(newOrder: List<CupUiModel>) {
        val reorderedCups = Cups(newOrder.map { it.toDomain() }).reorderRanks()
        _cupsUiState.value = MulKkamUiState.Success(reorderedCups.toUi())
    }

    fun updateCup(updatedCup: CupUiModel?) {
        val currentCups = cupsUiState.value?.toSuccessDataOrNull()?.cups ?: return
        if (updatedCup == null) return

        val newCups =
            currentCups.map { cup ->
                if (cup.rank == updatedCup.rank) updatedCup else cup
            }

        _cupsUiState.value =
            MulKkamUiState.Success(
                CupsUiModel(
                    newCups,
                    cupsUiState.value?.toSuccessDataOrNull()?.isAddable == true,
                ),
            )
    }

    fun deleteCup(rank: Int) {
        val currentCups = cupsUiState.value?.toSuccessDataOrNull()?.cups ?: return
        _cupsUiState.value = MulKkamUiState.Success(Cups(currentCups.filter { it.rank != rank }.map { it.toDomain() }).toUi())
    }

    fun addCup(newCup: CupUiModel) {
        val currentCups = cupsUiState.value?.toSuccessDataOrNull()?.cups ?: return
        _cupsUiState.value = MulKkamUiState.Success(Cups(currentCups.plus(newCup).map { it.toDomain() }).toUi())
    }
}
