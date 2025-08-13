package com.mulkkam.ui.settingtargetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.settingtargetamount.model.TargetAmountUiModel
import com.mulkkam.ui.util.MutableSingleLiveData
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingTargetAmountViewModel : ViewModel() {
    private val _targetInfoUiState = MutableLiveData<MulKkamUiState<TargetAmountUiModel>>(MulKkamUiState.Idle)
    val targetInfoUiState: LiveData<MulKkamUiState<TargetAmountUiModel>> get() = _targetInfoUiState

    private val _targetAmountInput = MutableLiveData<TargetAmount>()
    val targetAmountInput: LiveData<TargetAmount> get() = _targetAmountInput

    private val _saveTargetAmountUiState = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val saveTargetAmountUiState: LiveData<MulKkamUiState<Unit>> get() = _saveTargetAmountUiState

    private val _isTargetAmountValid: MutableLiveData<Boolean> = MutableLiveData()
    val isTargetAmountValid: LiveData<Boolean> get() = _isTargetAmountValid

    private val _onTargetAmountValidationError: MutableSingleLiveData<MulKkamError> =
        MutableSingleLiveData()
    val onTargetAmountValidationError: MutableSingleLiveData<MulKkamError>
        get() = _onTargetAmountValidationError

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
                    recommendedTargetAmount = recommended,
                    previousTargetAmount = previous,
                )
            }.onSuccess { targetAmountUiModel ->
                _targetInfoUiState.value = MulKkamUiState.Success(targetAmountUiModel)
                _targetAmountInput.value = TargetAmount(targetAmountUiModel.previousTargetAmount)
            }.onFailure {
                _targetInfoUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateTargetAmount(newTargetAmount: Int) {
        runCatching {
            _targetAmountInput.value = TargetAmount(newTargetAmount)
        }.onSuccess {
            _isTargetAmountValid.value = true
        }.onFailure { error ->
            _isTargetAmountValid.value = false
            _onTargetAmountValidationError.setValue(
                error as? TargetAmountError ?: MulKkamError.Unknown,
            )
        }
    }

    fun saveTargetAmount() {
        val amount = _targetAmountInput.value ?: return
        if (_saveTargetAmountUiState.value is MulKkamUiState.Loading) return

        viewModelScope.launch {
            _saveTargetAmountUiState.value = MulKkamUiState.Loading
            runCatching {
                intakeRepository.patchIntakeTarget(amount.amount).getOrError()
            }.onSuccess {
                _saveTargetAmountUiState.value = MulKkamUiState.Success(Unit)

                targetInfoUiState.value?.toSuccessDataOrNull()?.let { current ->
                    _targetInfoUiState.value =
                        MulKkamUiState.Success(
                            current.copy(previousTargetAmount = amount.amount),
                        )
                }
            }.onFailure {
                _saveTargetAmountUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
