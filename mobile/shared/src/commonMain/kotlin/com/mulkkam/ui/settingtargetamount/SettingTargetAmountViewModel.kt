package com.mulkkam.ui.settingtargetamount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.intake.TargetAmount.Companion.EMPTY_TARGET_AMOUNT
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.settingtargetamount.model.TargetAmountUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingTargetAmountViewModel(
    private val intakeRepository: IntakeRepository,
    private val membersRepository: MembersRepository,
    private val logger: Logger,
) : ViewModel() {
    private var targetAmountInput: TargetAmount = EMPTY_TARGET_AMOUNT

    private val _targetInfoUiState: MutableStateFlow<MulKkamUiState<TargetAmountUiModel>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val targetInfoUiState: StateFlow<MulKkamUiState<TargetAmountUiModel>> get() = _targetInfoUiState.asStateFlow()

    private val _saveTargetAmountUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val saveTargetAmountUiState: StateFlow<MulKkamUiState<Unit>> get() = _saveTargetAmountUiState.asStateFlow()

    private val _targetAmountValidityUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val targetAmountValidityUiState: StateFlow<MulKkamUiState<Unit>> get() = _targetAmountValidityUiState.asStateFlow()

    init {
        loadInitialTargetInfo()
    }

    private fun loadInitialTargetInfo() {
        if (_targetInfoUiState.value is MulKkamUiState.Loading) return

        viewModelScope.launch {
            _targetInfoUiState.value = MulKkamUiState.Loading
            runCatching {
                val recommendedDeferred =
                    async { intakeRepository.getIntakeAmountRecommended().getOrError() }
                val nicknameDeferred = async { membersRepository.getMembersNickname().getOrError() }
                val previousDeferred = async { intakeRepository.getIntakeTarget().getOrError() }

                val recommended = recommendedDeferred.await()
                val nickname = nicknameDeferred.await()
                val previous = previousDeferred.await()

                TargetAmountUiModel(
                    nickname = nickname,
                    recommendedTargetAmount = TargetAmount(recommended),
                    previousTargetAmount = TargetAmount(previous),
                )
            }.onSuccess { targetAmountUiModel ->
                _targetInfoUiState.value = MulKkamUiState.Success(targetAmountUiModel)
                targetAmountInput = targetAmountUiModel.previousTargetAmount
            }.onFailure {
                _targetInfoUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateTargetAmount(newTargetAmount: Int) {
        runCatching {
            targetAmountInput = TargetAmount(newTargetAmount)
        }.onSuccess {
            _targetAmountValidityUiState.value = MulKkamUiState.Success(Unit)
        }.onFailure { error ->
            _targetAmountValidityUiState.value = MulKkamUiState.Failure(error.toMulKkamError())
        }
    }

    fun saveTargetAmount() {
        val amount = targetAmountInput.value
        if (_saveTargetAmountUiState.value is MulKkamUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                logger.info(LogEvent.USER_ACTION, "Saving target amount: $amount")
                _saveTargetAmountUiState.value = MulKkamUiState.Loading
                intakeRepository.patchIntakeTarget(amount).getOrError()
            }.onSuccess {
                _saveTargetAmountUiState.value = MulKkamUiState.Success(Unit)

                targetInfoUiState.value.toSuccessDataOrNull()?.let { current ->
                    _targetInfoUiState.value =
                        MulKkamUiState.Success(
                            current.copy(previousTargetAmount = targetAmountInput),
                        )
                }
            }.onFailure {
                _saveTargetAmountUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
