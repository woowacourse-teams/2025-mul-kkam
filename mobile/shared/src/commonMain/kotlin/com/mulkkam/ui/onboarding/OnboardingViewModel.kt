package com.mulkkam.ui.onboarding

import androidx.lifecycle.ViewModel
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.model.NicknameValidationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel : ViewModel() {
    private val _onboardingInfo: MutableStateFlow<OnboardingInfo> =
        MutableStateFlow(OnboardingInfo())
    val onboardingInfo: StateFlow<OnboardingInfo> = _onboardingInfo.asStateFlow()

    private val _isTermsOfServiceAgreed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isTermsOfServiceAgreed: StateFlow<Boolean> = _isTermsOfServiceAgreed.asStateFlow()

    private val _isPrivacyPolicyAgreed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPrivacyPolicyAgreed: StateFlow<Boolean> = _isPrivacyPolicyAgreed.asStateFlow()

    private val _nicknameValidationState: MutableStateFlow<NicknameValidationUiState> =
        MutableStateFlow(NicknameValidationUiState.NONE)
    val nicknameValidationState: StateFlow<NicknameValidationUiState>
        get() = _nicknameValidationState.asStateFlow()

    fun updateTermsAgreement(
        isServiceAgreed: Boolean = false,
        isPrivacyPolicyAgreed: Boolean = false,
        isMarketingNotificationAgreed: Boolean,
        isNightNotificationAgreed: Boolean,
    ) {
        _isTermsOfServiceAgreed.value = isServiceAgreed
        _isPrivacyPolicyAgreed.value = isPrivacyPolicyAgreed

        _onboardingInfo.value =
            _onboardingInfo.value.copy(
                isMarketingNotificationAgreed = isMarketingNotificationAgreed,
                isNightNotificationAgreed = isNightNotificationAgreed,
            )
    }

    fun updateNickname(
        nickname: String?,
        nicknameValidationUiState: NicknameValidationUiState,
    ) {
        _onboardingInfo.value = onboardingInfo.value.copy(nickname = nickname?.let { Nickname(it) })
        _nicknameValidationState.value = nicknameValidationUiState
    }

    fun updateBioInfo(
        gender: Gender?,
        weight: BioWeight?,
    ) {
        _onboardingInfo.value = onboardingInfo.value.copy(gender = gender, weight = weight)
    }

    fun updateTargetAmount(targetAmount: Int?) {
        _onboardingInfo.value = onboardingInfo.value.copy(targetAmount = targetAmount)
    }
}
