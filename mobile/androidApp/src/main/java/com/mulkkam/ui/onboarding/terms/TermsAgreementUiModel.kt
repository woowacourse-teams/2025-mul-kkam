package com.mulkkam.ui.onboarding.terms

import androidx.annotation.StringRes

data class TermsAgreementUiModel(
    @StringRes val labelId: Int,
    val isRequired: Boolean,
    val isChecked: Boolean = false,
    @StringRes val uri: Int = 0,
)
