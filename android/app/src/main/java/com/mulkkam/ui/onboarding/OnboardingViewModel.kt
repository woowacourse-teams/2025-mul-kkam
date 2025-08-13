package com.mulkkam.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val _onboardingState = MutableLiveData<OnboardingStep>()
    val onboardingState: LiveData<OnboardingStep> get() = _onboardingState

    val canSkip: LiveData<Boolean> =
        onboardingState.map { state -> state == OnboardingStep.BIO_INFO }

    private val _onCompleteOnboarding = MutableSingleLiveData<Unit>()
    val onCompleteOnboarding: SingleLiveData<Unit>
        get() = _onCompleteOnboarding

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
        viewModelScope.launch {
            val result = RepositoryInjection.membersRepository.postMembers(onboardingInfo)
            runCatching {
                result.getOrError()
                _onCompleteOnboarding.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
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

    fun updateTargetAmount(targetAmount: Int) {
        onboardingInfo = onboardingInfo.copy(targetAmount = targetAmount)
    }

    companion object {
        const val STEP_OFFSET = 1
        const val FIRST_ONBOARDING_INDEX = 0
    }
}
