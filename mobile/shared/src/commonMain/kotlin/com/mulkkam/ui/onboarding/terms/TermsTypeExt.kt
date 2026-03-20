package com.mulkkam.ui.onboarding.terms

import com.mulkkam.ui.onboarding.terms.model.TermsType
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.terms_agree_marketing
import mulkkam.shared.generated.resources.terms_agree_night_notification
import mulkkam.shared.generated.resources.terms_agree_privacy
import mulkkam.shared.generated.resources.terms_agree_service
import mulkkam.shared.generated.resources.terms_marketing
import mulkkam.shared.generated.resources.terms_night_notification
import mulkkam.shared.generated.resources.terms_privacy
import mulkkam.shared.generated.resources.terms_service
import org.jetbrains.compose.resources.StringResource

fun TermsType.toResourceIds(): Pair<StringResource, StringResource> =
    when (this) {
        TermsType.SERVICE -> Res.string.terms_agree_service to Res.string.terms_service
        TermsType.PRIVACY -> Res.string.terms_agree_privacy to Res.string.terms_privacy
        TermsType.NIGHT_NOTIFICATION -> Res.string.terms_agree_night_notification to Res.string.terms_night_notification
        TermsType.MARKETING -> Res.string.terms_agree_marketing to Res.string.terms_marketing
    }
