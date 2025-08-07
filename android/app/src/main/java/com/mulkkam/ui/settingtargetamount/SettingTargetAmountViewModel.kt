package com.mulkkam.ui.settingtargetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.TargetAmount
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingTargetAmountViewModel : ViewModel() {
    private var _targetAmount = MutableLiveData<TargetAmount>()
    val targetAmount: LiveData<TargetAmount> get() = _targetAmount

    private val _onSaveTargetAmount = MutableSingleLiveData<Unit>()
    val onSaveTargetAmount: SingleLiveData<Unit> get() = _onSaveTargetAmount

    private val _onRecommendationReady = MutableSingleLiveData<Unit>()
    val onRecommendationReady: SingleLiveData<Unit> get() = _onRecommendationReady

    private val _isTargetAmountValid = MutableLiveData<Boolean?>()
    val isTargetAmountValid: LiveData<Boolean?> get() = _isTargetAmountValid

    private val _recommendedTargetAmount = MutableLiveData<Int>()
    val recommendedTargetAmount: LiveData<Int> get() = _recommendedTargetAmount

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    init {
        viewModelScope.launch {
            val recommendedTargetAmountResult = intakeRepository.getIntakeAmountRecommended()
            val nicknameResult = membersRepository.getMembersNickname()
            runCatching {
                _recommendedTargetAmount.value = recommendedTargetAmountResult.getOrError()
                _nickname.value = nicknameResult.getOrError()
                _onRecommendationReady.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun updateTargetAmount(newTargetAmount: Int?) {
        runCatching {
            newTargetAmount?.let {
                _targetAmount.value = TargetAmount(newTargetAmount)
                _isTargetAmountValid.value = true
            } ?: run {
                _isTargetAmountValid.value = null
            }
        }.onFailure {
            _isTargetAmountValid.value = false
        }
    }

    fun saveTargetAmount() {
        viewModelScope.launch {
            val result = targetAmount.value?.let { intakeRepository.patchIntakeTarget(it.amount) }
            runCatching {
                result?.getOrError()
                _onSaveTargetAmount.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
