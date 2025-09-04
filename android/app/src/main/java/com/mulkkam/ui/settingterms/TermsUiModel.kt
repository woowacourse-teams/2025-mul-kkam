package com.mulkkam.ui.settingterms

import androidx.annotation.StringRes

data class TermsUiModel(
    @StringRes val labelId: Int,
    @StringRes val uri: Int = 0,
)
