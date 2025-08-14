package com.mulkkam.ui.onboarding.targetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
import com.mulkkam.ui.util.MutableSingleLiveData
import kotlinx.coroutines.launch

class TargetAmountViewModel : ViewModel() {
    private var _targetAmount: MutableLiveData<TargetAmount> = MutableLiveData()
    val targetAmount: LiveData<TargetAmount> get() = _targetAmount

    private val _recommendedTargetAmount: MutableLiveData<Int> = MutableLiveData()
    val recommendedTargetAmount: MutableLiveData<Int>
        get() = _recommendedTargetAmount

    private val _isTargetAmountValid: MutableLiveData<Boolean?> = MutableLiveData()
    val isTargetAmountValid: LiveData<Boolean?> get() = _isTargetAmountValid

    private val _onTargetAmountValidationError: MutableSingleLiveData<MulKkamError> =
        MutableSingleLiveData()
    val onTargetAmountValidationError: MutableSingleLiveData<MulKkamError>
        get() = _onTargetAmountValidationError

    fun getRecommendedTargetAmount(
        gender: Gender?,
        weight: BioWeight?,
    ) {
        viewModelScope.launch {
            runCatching {
                RepositoryInjection.intakeRepository
                    .getIntakeAmountTargetRecommended(
                        gender,
                        weight,
                    ).getOrError()
            }.onSuccess { recommendedTargetAmount ->
                _recommendedTargetAmount.value = recommendedTargetAmount
            }.onFailure {
                // TODO: 에러 처리
            }
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
}
