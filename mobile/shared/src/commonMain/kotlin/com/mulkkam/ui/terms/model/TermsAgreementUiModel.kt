package com.mulkkam.ui.terms.model

data class TermsAgreementUiModel(
    val type: TermsType,
    val isRequired: Boolean,
    val isChecked: Boolean = false,
)
