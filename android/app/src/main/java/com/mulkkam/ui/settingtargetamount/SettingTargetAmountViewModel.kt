package com.mulkkam.ui.settingtargetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.intake.TargetAmount.Companion.EMPTY_TARGET_AMOUNT
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingtargetamount.model.TargetAmountUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingTargetAmountViewModel : ViewModel() {
    private val _targetInfoUiState: MutableLiveData<MulKkamUiState<TargetAmountUiModel>> =
        MutableLiveData<MulKkamUiState<TargetAmountUiModel>>(MulKkamUiState.Idle)
    val targetInfoUiState: LiveData<MulKkamUiState<TargetAmountUiModel>> get() = _targetInfoUiState

    private val _targetAmountInput: MutableLiveData<TargetAmount> = MutableLiveData<TargetAmount>(EMPTY_TARGET_AMOUNT)
    val targetAmountInput: LiveData<TargetAmount> get() = _targetAmountInput

    private val _saveTargetAmountUiState: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
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
                val recommendedDeferred = async { intakeRepository.getIntakeAmountRecommended().getOrError() }
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
            _saveTargetAmountUiState.value = MulKkamUiState.Loading
            runCatching {
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
