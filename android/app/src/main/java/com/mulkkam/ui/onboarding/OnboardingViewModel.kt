package com.mulkkam.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData

class OnboardingViewModel : ViewModel() {
    private val _onboardingState = MutableLiveData<OnboardingStep>()
    val onboardingState: LiveData<OnboardingStep> get() = _onboardingState

    val canSkip: LiveData<Boolean> =
        onboardingState.map { state -> state == OnboardingStep.BIO_INFO }

    private val _onCompleteOnboarding = MutableSingleLiveData<Unit>()
    val onCompleteOnboarding: SingleLiveData<Unit>
        get() = _onCompleteOnboarding

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
        // TODO: 회원가입 API 호출
        runCatching {
            _onCompleteOnboarding.setValue(Unit)
        }
    }

    companion object {
        const val STEP_OFFSET = 1
        const val FIRST_ONBOARDING_INDEX = 0
    }
}
