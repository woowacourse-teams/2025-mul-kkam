package com.mulkkam.ui.onboarding.cups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.OnboardingRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.setting.cups.model.CupUiModel
import com.mulkkam.ui.setting.cups.model.CupsUiModel
import com.mulkkam.ui.setting.cups.model.toDomain
import com.mulkkam.ui.setting.cups.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CupsViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val cupsRepository: CupsRepository,
    private val logger: Logger,
) : ViewModel() {
    private val _saveOnboardingUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val saveOnboardingUiState: StateFlow<MulKkamUiState<Unit>>
        get() = _saveOnboardingUiState.asStateFlow()

    var onboardingInfo: OnboardingInfo = OnboardingInfo()
        private set

    private var _cupsUiState: MutableStateFlow<MulKkamUiState<CupsUiModel>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val cupsUiState: StateFlow<MulKkamUiState<CupsUiModel>> get() = _cupsUiState.asStateFlow()

    init {
        loadCups()
    }

    fun updateOnboardingInfo(onboardingInfo: OnboardingInfo) {
        this.onboardingInfo = onboardingInfo
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

    fun completeOnboarding() {
        if (saveOnboardingUiState.value is MulKkamUiState.Loading) return
        onboardingInfo =
            onboardingInfo.copy(
                cups =
                    cupsUiState.value
                        .toSuccessDataOrNull()
                        ?.cups
                        ?.map { it.toDomain() }
                        ?: emptyList(),
            )
        viewModelScope.launch {
            runCatching {
                logger.info(LogEvent.ONBOARDING, "Onboarding submission succeeded")
                logger.info(
                    LogEvent.ONBOARDING,
                    "isMarketingNotificationAgreed: ${onboardingInfo.isMarketingNotificationAgreed}, isNightNotificationAgreed: ${onboardingInfo.isNightNotificationAgreed}",
                )
                _saveOnboardingUiState.value = MulKkamUiState.Loading
                onboardingRepository.postOnboarding(onboardingInfo).getOrError()
            }.onSuccess {
                _saveOnboardingUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure {
                _saveOnboardingUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
