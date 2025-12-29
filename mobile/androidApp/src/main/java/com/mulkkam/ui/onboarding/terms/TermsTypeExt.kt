package com.mulkkam.ui.onboarding.terms

import com.mulkkam.R
import com.mulkkam.ui.terms.model.TermsType

fun TermsType.toResourceIds(): Pair<Int, Int> {
    return when (this) {
        TermsType.SERVICE -> R.string.terms_agree_service to R.string.terms_service
        TermsType.PRIVACY -> R.string.terms_agree_privacy to R.string.terms_privacy
        TermsType.NIGHT_NOTIFICATION -> R.string.terms_agree_night_notification to R.string.terms_night_notification
        TermsType.MARKETING -> R.string.terms_agree_marketing to R.string.terms_marketing
    }
}
