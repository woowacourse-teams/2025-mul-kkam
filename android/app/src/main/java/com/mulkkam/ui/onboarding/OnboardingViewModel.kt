package com.mulkkam.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.onboardingRepository
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.toDomain
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val _onboardingState: MutableLiveData<OnboardingStep> = MutableLiveData<OnboardingStep>()
    val onboardingState: LiveData<OnboardingStep> get() = _onboardingState

    val canSkip: LiveData<Boolean> =
        onboardingState.map { state -> state == OnboardingStep.BIO_INFO }

    private val _saveOnboardingUiState: MutableLiveData<MulKkamUiState<Unit>> = MutableLiveData<MulKkamUiState<Unit>>(MulKkamUiState.Idle)
    val saveOnboardingUiState: LiveData<MulKkamUiState<Unit>>
        get() = _saveOnboardingUiState

    var onboardingInfo: OnboardingInfo = OnboardingInfo()
        private set

    fun updateOnboardingState(state: OnboardingStep) {
        _onboardingState.value = state
    }

    fun moveToNextStep() {
        updateOnboardingState(
            OnboardingStep.entries.getOrNull(
                onboardingState.value?.ordinal?.plus(
                    STEP_OFFSET,
                ) ?: FIRST_ONBOARDING_INDEX,
            ) ?: OnboardingStep.TERMS,
        )
    }

    fun moveToPreviousStep() {
        updateOnboardingState(
            OnboardingStep.entries.getOrNull(
                onboardingState.value?.ordinal?.minus(
                    STEP_OFFSET,
                ) ?: FIRST_ONBOARDING_INDEX,
            ) ?: OnboardingStep.TERMS,
        )
    }

    fun completeOnboarding() {
        if (saveOnboardingUiState.value is MulKkamUiState.Loading) return
        _saveOnboardingUiState.value = MulKkamUiState.Loading
        viewModelScope.launch {
            val result = onboardingRepository.postOnboarding(onboardingInfo)
            runCatching {
                result.getOrError()
                _saveOnboardingUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure {
                _saveOnboardingUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun updateTermsAgreementState(
        isMarketingNotificationAgreed: Boolean,
        isNightNotificationAgreed: Boolean,
    ) {
        onboardingInfo =
            onboardingInfo.copy(
                isMarketingNotificationAgreed = isMarketingNotificationAgreed,
                isNightNotificationAgreed = isNightNotificationAgreed,
            )
    }

    fun updateNickname(nickname: String) {
        onboardingInfo = onboardingInfo.copy(nickname = Nickname(nickname))
    }

    fun updateBioInfo(
        gender: Gender?,
        weight: BioWeight?,
    ) {
        onboardingInfo = onboardingInfo.copy(gender = gender, weight = weight)
    }

    fun clearBioInfo() {
        onboardingInfo = onboardingInfo.copy(gender = null, weight = null)
    }

    fun updateTargetAmount(targetAmount: Int) {
        onboardingInfo = onboardingInfo.copy(targetAmount = targetAmount)
    }

    fun updateCups(cups: List<CupUiModel>) {
        onboardingInfo = onboardingInfo.copy(cups = cups.map { it.toDomain() })
    }

    companion object {
        const val STEP_OFFSET = 1
        const val FIRST_ONBOARDING_INDEX = 0
    }
}
