package com.mulkkam.ui.onboarding

import androidx.fragment.app.Fragment
import com.mulkkam.ui.onboarding.bioinfo.BioInfoFragment
import com.mulkkam.ui.onboarding.nickname.NicknameFragment
import com.mulkkam.ui.onboarding.targetamount.TargetAmountFragment
import com.mulkkam.ui.onboarding.terms.TermsFragment

enum class OnboardingStep(
    val create: () -> Fragment,
) {
    TERMS({ TermsFragment() }),
    NICKNAME({ NicknameFragment() }),
    PHYSICAL_INFO({ BioInfoFragment() }),
    TARGET_AMOUNT({ TargetAmountFragment() }),
}
