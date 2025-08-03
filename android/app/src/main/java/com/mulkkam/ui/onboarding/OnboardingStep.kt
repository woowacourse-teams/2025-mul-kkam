package com.mulkkam.ui.onboarding

import androidx.fragment.app.Fragment
import com.mulkkam.ui.onboarding.nickname.NicknameFragment
import com.mulkkam.ui.onboarding.physicalinfo.PhysicalInfoFragment
import com.mulkkam.ui.onboarding.targetamount.TargetAmountFragment
import com.mulkkam.ui.onboarding.terms.TermsFragment

enum class OnboardingStep(
    val create: () -> Fragment,
) {
    TERMS({ TermsFragment() }),
    NICKNAME({ NicknameFragment() }),
    PHYSICAL_INFO({ PhysicalInfoFragment() }),
    TARGET_AMOUNT({ TargetAmountFragment() }),
}
