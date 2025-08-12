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
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingTargetAmountViewModel : ViewModel() {
    private var _targetAmount: MutableLiveData<TargetAmount> = MutableLiveData()
    val targetAmount: LiveData<TargetAmount> get() = _targetAmount

    private val _previousTargetAmount: MutableLiveData<Int> = MutableLiveData()
    val previousTargetAmount: LiveData<Int> get() = _previousTargetAmount

    private val _isTargetAmountValid: MutableLiveData<Boolean> = MutableLiveData()
    val isTargetAmountValid: LiveData<Boolean> get() = _isTargetAmountValid

    private val _recommendedTargetAmount: MutableLiveData<Int> = MutableLiveData()
    val recommendedTargetAmount: LiveData<Int> get() = _recommendedTargetAmount

    private val _nickname: MutableLiveData<String> = MutableLiveData()
    val nickname: LiveData<String> get() = _nickname

    private val _onRecommendationReady: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val onRecommendationReady: SingleLiveData<Unit> get() = _onRecommendationReady

    private val _onSaveTargetAmount: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val onSaveTargetAmount: SingleLiveData<Unit> get() = _onSaveTargetAmount

    private val _onTargetAmountValidationError: MutableSingleLiveData<MulKkamError> =
        MutableSingleLiveData()
    val onTargetAmountValidationError: MutableSingleLiveData<MulKkamError>
        get() = _onTargetAmountValidationError

    init {
        viewModelScope.launch {
            loadTargetAmountRecommended()
            loadTargetAmount()
        }
    }

    private suspend fun loadTargetAmountRecommended() {
        runCatching {
            val recommendedTargetAmountResult =
                intakeRepository.getIntakeAmountRecommended().getOrError()
            val nicknameResult = membersRepository.getMembersNickname().getOrError()
            _recommendedTargetAmount.value = recommendedTargetAmountResult
            _nickname.value = nicknameResult
        }.onSuccess {
            _onRecommendationReady.setValue(Unit)
        }.onFailure {
            // TODO: 에러 처리
        }
    }

    private suspend fun loadTargetAmount() {
        runCatching {
            intakeRepository.getIntakeTarget().getOrError()
        }.onSuccess { amount ->
            _previousTargetAmount.value = amount
        }.onFailure {
            // TODO: 에러 처리
        }
    }

    fun updateTargetAmount(newTargetAmount: Int) {
        runCatching {
            _targetAmount.value = TargetAmount(newTargetAmount)
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
        viewModelScope.launch {
            runCatching {
                val result =
                    targetAmount.value?.let { intakeRepository.patchIntakeTarget(it.amount) }
                result?.getOrError()
            }.onSuccess {
                _onSaveTargetAmount.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
