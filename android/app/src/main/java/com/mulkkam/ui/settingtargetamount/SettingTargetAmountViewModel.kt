package com.mulkkam.ui.settingtargetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingtargetamount.model.TargetAmountUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingTargetAmountViewModel
    @Inject
    constructor(
        private val intakeRepository: IntakeRepository,
        private val membersRepository: MembersRepository,
        private val logger: Logger,
    ) : ViewModel() {
        private val _targetInfoUiState: MutableLiveData<MulKkamUiState<TargetAmountUiModel>> =
            MutableLiveData<MulKkamUiState<TargetAmountUiModel>>(MulKkamUiState.Idle)
        val targetInfoUiState: LiveData<MulKkamUiState<TargetAmountUiModel>> get() = _targetInfoUiState

        private val _targetAmountInput: MutableLiveData<TargetAmount> =
            MutableLiveData<TargetAmount>(EMPTY_TARGET_AMOUNT)
        val targetAmountInput: LiveData<TargetAmount> get() = _targetAmountInput

        private val _saveTargetAmountUiState: MutableLiveData<MulKkamUiState<Unit>> =
            MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
        val saveTargetAmountUiState: LiveData<MulKkamUiState<Unit>> get() = _saveTargetAmountUiState

        private val _targetAmountValidityUiState: MutableLiveData<MulKkamUiState<Unit>> =
            MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
        val targetAmountValidityUiState: LiveData<MulKkamUiState<Unit>> get() = _targetAmountValidityUiState

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
                    _targetAmountInput.value = targetAmountUiModel.previousTargetAmount
                }.onFailure {
                    _targetInfoUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                }
            }
        }

        fun updateTargetAmount(newTargetAmount: Int) {
            runCatching {
                _targetAmountInput.value = TargetAmount(newTargetAmount)
            }.onSuccess {
                _targetAmountValidityUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure { error ->
                _targetAmountValidityUiState.value = MulKkamUiState.Failure(error.toMulKkamError())
            }
        }

        fun saveTargetAmount() {
            val amount = _targetAmountInput.value ?: return
            if (_saveTargetAmountUiState.value is MulKkamUiState.Loading) return

            viewModelScope.launch {
                runCatching {
                    logger.info(LogEvent.USER_ACTION, "Saving target amount: $amount")
                    _saveTargetAmountUiState.value = MulKkamUiState.Loading
                    intakeRepository.patchIntakeTarget(amount.value).getOrError()
                }.onSuccess {
                    _saveTargetAmountUiState.value = MulKkamUiState.Success(Unit)

                    targetInfoUiState.value?.toSuccessDataOrNull()?.let { current ->
                        _targetInfoUiState.value =
                            MulKkamUiState.Success(
                                current.copy(previousTargetAmount = amount),
                            )
                    }
                }.onFailure {
                    _saveTargetAmountUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                }
            }
        }
    }
