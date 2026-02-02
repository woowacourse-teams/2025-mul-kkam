package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.onboarding.targetamount.model.TargetAmountOnboardingUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TargetAmountViewModel(
    private val intakeRepository: IntakeRepository,
) : ViewModel() {
    private var targetAmountInput: TargetAmount? = null
    private val _targetAmountOnboardingUiState: MutableStateFlow<MulKkamUiState<TargetAmountOnboardingUiModel>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val targetAmountOnboardingUiState: StateFlow<MulKkamUiState<TargetAmountOnboardingUiModel>>
        get() = _targetAmountOnboardingUiState.asStateFlow()

    private val _targetAmountValidityUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val targetAmountValidityUiState: StateFlow<MulKkamUiState<Unit>>
        get() = _targetAmountValidityUiState.asStateFlow()

    fun loadRecommendedTargetAmount(
        nickname: String,
        gender: Gender?,
        weight: BioWeight?,
    ) {
        if (_targetAmountOnboardingUiState.value is MulKkamUiState.Loading) return

        viewModelScope.launch {
            _targetAmountOnboardingUiState.value = MulKkamUiState.Loading
            runCatching {
                val amount =
                    intakeRepository
                        .getIntakeAmountTargetRecommended(gender, weight)
                        .getOrError()
                TargetAmountOnboardingUiModel(
                    nickname = nickname,
                    recommendedTargetAmount = TargetAmount(amount),
                )
            }.onSuccess { targetAmountOnboardingUiModel ->
                _targetAmountOnboardingUiState.value =
                    MulKkamUiState.Success(targetAmountOnboardingUiModel)
                updateTargetAmount(targetAmountOnboardingUiModel.recommendedTargetAmount.value)
            }.onFailure {
                _targetAmountOnboardingUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateTargetAmount(newTargetAmount: Int?) {
        if (newTargetAmount == null) {
            targetAmountInput = null
            _targetAmountValidityUiState.value = MulKkamUiState.Idle
            return
        }

        runCatching {
            TargetAmount(newTargetAmount)
        }.onSuccess { targetAmount ->
            targetAmountInput = targetAmount
            _targetAmountValidityUiState.value = MulKkamUiState.Success(Unit)
        }.onFailure {
            _targetAmountValidityUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
        }
    }
}
