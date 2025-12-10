package com.mulkkam.ui.onboarding.cups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupsUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import com.mulkkam.ui.settingcups.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CupsViewModel
    @Inject
    constructor(
        private val cupsRepository: CupsRepository,
    ) : ViewModel() {
        private var _cupsUiState: MutableStateFlow<MulKkamUiState<CupsUiModel>> =
            MutableStateFlow(MulKkamUiState.Idle)
        val cupsUiState: StateFlow<MulKkamUiState<CupsUiModel>> get() = _cupsUiState.asStateFlow()

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

        fun updateCup(updatedCup: CupUiModel) {
            val currentCups = cupsUiState.value.toSuccessDataOrNull()?.cups ?: return

            val newCups =
                currentCups.map { cup ->
                    if (cup.rank == updatedCup.rank) updatedCup else cup
                }

            _cupsUiState.value =
                MulKkamUiState.Success(
                    CupsUiModel(
                        newCups,
                        cupsUiState.value.toSuccessDataOrNull()?.isAddable == true,
                    ),
                )
        }

        fun deleteCup(rank: Int) {
            val currentCups = cupsUiState.value.toSuccessDataOrNull()?.cups ?: return
            val updatedCups =
                currentCups
                    .asSequence()
                    .filterNot { it.rank == rank }
                    .map { it.toDomain() }
                    .toList()

            _cupsUiState.value =
                MulKkamUiState.Success(
                    Cups(updatedCups).toUi(),
                )
        }

        fun addCup(newCup: CupUiModel) {
            val currentCups = cupsUiState.value.toSuccessDataOrNull() ?: return
            val addedCups = currentCups.copy(cups = currentCups.cups + newCup)
            val updatedCups =
                Cups(addedCups.cups.map { it.toDomain() }).reorderRanks().toUi()

            _cupsUiState.value = MulKkamUiState.Success(updatedCups)
        }
    }
