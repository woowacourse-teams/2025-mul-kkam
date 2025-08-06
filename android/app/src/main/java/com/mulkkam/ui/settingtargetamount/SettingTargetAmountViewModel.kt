package com.mulkkam.ui.settingtargetamount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class SettingTargetAmountViewModel : ViewModel() {
    private var _goal: MutableLiveData<Int> = MutableLiveData(0)
    val goal: LiveData<Int> get() = _goal

    private val _onSaveTargetAmount = MutableSingleLiveData<Unit>()
    val onSaveTargetAmount: SingleLiveData<Unit> get() = _onSaveTargetAmount

    private val _onRecommendationReady = MutableSingleLiveData<Unit>()
    val onRecommendationReady: SingleLiveData<Unit> get() = _onRecommendationReady

    var recommendedTargetAmount: Int? = null
        private set
    var nickname: String? = null
        private set

    init {
        viewModelScope.launch {
            val recommendedTargetAmountResult = intakeRepository.getIntakeAmountRecommended()
            val nicknameResult = membersRepository.getMembersNickname()
            runCatching {
                recommendedTargetAmount = recommendedTargetAmountResult.getOrError()
                nickname = nicknameResult.getOrError()
                _onRecommendationReady.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun updateGoal(newGoal: Int) {
        _goal.value = newGoal
    }

    fun saveGoal() {
        viewModelScope.launch {
            val result = goal.value?.let { intakeRepository.patchIntakeTarget(it) }
            runCatching {
                result?.getOrError()
                _onSaveTargetAmount.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
