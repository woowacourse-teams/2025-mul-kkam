package com.mulkkam.ui.settingterms

import com.mulkkam.R
import com.mulkkam.ui.settingterms.model.SettingTermsType

fun SettingTermsType.toLabelResource(): Int =
    when (this) {
        SettingTermsType.SERVICE -> R.string.setting_terms_agree_service
        SettingTermsType.PRIVACY -> R.string.setting_terms_agree_privacy
        SettingTermsType.HEALTH_CONNECT -> R.string.setting_terms_agree_health_connect
    }

fun SettingTermsType.toUriResource(): Int? =
    when (this) {
        SettingTermsType.SERVICE -> R.string.terms_service
        SettingTermsType.PRIVACY -> R.string.terms_privacy
        SettingTermsType.HEALTH_CONNECT -> null
    }
