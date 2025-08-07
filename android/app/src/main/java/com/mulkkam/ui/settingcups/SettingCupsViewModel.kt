package com.mulkkam.ui.settingcups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.cupsRepository
import com.mulkkam.domain.model.Cups
import com.mulkkam.domain.model.IntakeType
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

    private fun loadCups() {
        viewModelScope.launch {
            val result = cupsRepository.getCups()
            runCatching {
                _cups.value = result.getOrError().toUi()
            }.onFailure {
                _cups.value = DUMMY_CUPS
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
            val result = cupsRepository.putCupsRank(reorderedCups)
            runCatching {
                if (reorderedCups != result.getOrError()) {
                    _cups.value = result.getOrError().toUi()
                }
            }.onFailure {
                _cups.value = cups.value
                // TODO: 예외 처리
            }
        }
    }

    companion object {
        private val DUMMY_CUPS =
            CupsUiModel(
                cups =
                    listOf(
                        CupUiModel(
                            id = 1L,
                            nickname = "컵 1",
                            amount = 250,
                            rank = 1,
                            intakeType = IntakeType.WATER,
                            emoji = "💧",
                        ),
                        CupUiModel(
                            id = 2L,
                            nickname = "컵 2",
                            amount = 500,
                            rank = 2,
                            intakeType = IntakeType.COFFEE,
                            emoji = "☕",
                        ),
                    ),
                isAddable = true,
            )
    }
}
