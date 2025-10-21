package com.mulkkam.ui.onboarding.targetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.onboarding.targetamount.model.TargetAmountOnboardingUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TargetAmountViewModel
    @Inject
    constructor(
        private val intakeRepository: IntakeRepository,
    ) : ViewModel() {
        private val _targetAmountOnboardingUiState =
            MutableLiveData<MulKkamUiState<TargetAmountOnboardingUiModel>>(MulKkamUiState.Idle)
        val targetAmountOnboardingUiState: LiveData<MulKkamUiState<TargetAmountOnboardingUiModel>> get() = _targetAmountOnboardingUiState

        private val _targetAmountInput = MutableLiveData<TargetAmount?>()
        val targetAmountInput: LiveData<TargetAmount?> get() = _targetAmountInput

        private val _targetAmountValidityUiState =
            MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
        val targetAmountValidityUiState: LiveData<MulKkamUiState<Unit>> get() = _targetAmountValidityUiState

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
                    _targetAmountInput.value = targetAmountOnboardingUiModel.recommendedTargetAmount
                    _targetAmountValidityUiState.value = MulKkamUiState.Success(Unit)
                }.onFailure {
                    _targetAmountOnboardingUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                }
            }
        }

        fun updateTargetAmount(newTargetAmount: Int?) {
            if (newTargetAmount == null) {
                _targetAmountInput.value = null
                _targetAmountValidityUiState.value = MulKkamUiState.Idle
                return
            }

            runCatching {
                TargetAmount(newTargetAmount)
            }.onSuccess { targetAmount ->
                _targetAmountInput.value = targetAmount
                _targetAmountValidityUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure {
                _targetAmountValidityUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
