package com.mulkkam.ui.onboarding.terms

data class TermsAgreementUiModel(
    val title: String,
    val isRequired: Boolean,
    val isChecked: Boolean = false,
)
