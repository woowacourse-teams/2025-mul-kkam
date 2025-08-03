package com.mulkkam.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class OnboardingViewModel : ViewModel() {
    private val _onboardingState = MutableLiveData<OnboardingStep>()
    val onboardingState: LiveData<OnboardingStep> get() = _onboardingState

    val canSkip: LiveData<Boolean> =
        onboardingState.map { state -> state == OnboardingStep.PHYSICAL_INFO }

    fun updateOnboardingState(state: OnboardingStep) {
        _onboardingState.value = state
    }

    fun moveToNextStep() {
        updateOnboardingState(
            OnboardingStep.entries.getOrNull(
                onboardingState.value?.ordinal?.plus(
                    1,
                ) ?: 0,
            ) ?: OnboardingStep.TERMS,
        )
    }
}
