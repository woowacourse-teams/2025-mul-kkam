package com.mulkkam.ui.settingterms.model

import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_terms_agree_health_connect
import mulkkam.shared.generated.resources.setting_terms_agree_privacy
import mulkkam.shared.generated.resources.setting_terms_agree_service
import org.jetbrains.compose.resources.StringResource

enum class SettingTermsType {
    SERVICE,
    PRIVACY,
    HEALTH_CONNECT,
}

fun SettingTermsType.toLabelResource(): StringResource =
    when (this) {
        SettingTermsType.SERVICE -> Res.string.setting_terms_agree_service
        SettingTermsType.PRIVACY -> Res.string.setting_terms_agree_privacy
        SettingTermsType.HEALTH_CONNECT -> Res.string.setting_terms_agree_health_connect
    }
