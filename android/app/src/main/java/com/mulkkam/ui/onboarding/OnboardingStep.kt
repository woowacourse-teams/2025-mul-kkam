package com.mulkkam.ui.onboarding

import androidx.fragment.app.Fragment
import com.mulkkam.ui.onboarding.bioinfo.BioInfoFragment
import com.mulkkam.ui.onboarding.nickname.NicknameFragment
import com.mulkkam.ui.onboarding.targetamount.TargetAmountFragment
import com.mulkkam.ui.onboarding.terms.TermsFragment

enum class OnboardingStep(
    val fragment: Class<out Fragment>,
) {
    TERMS(TermsFragment::class.java),
    NICKNAME(NicknameFragment::class.java),
    BIO_INFO(BioInfoFragment::class.java),
    TARGET_AMOUNT(TargetAmountFragment::class.java),
}
