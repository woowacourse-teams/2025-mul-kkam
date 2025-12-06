package com.mulkkam.ui.settingterms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.setting.component.SettingNormalItem
import com.mulkkam.ui.setting.component.SettingTopAppBar

@Composable
fun SettingTermsScreen(
    terms: List<TermsUiModel>,
    onTermsClick: (TermsUiModel) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = { SettingTopAppBar(titleResId = R.string.setting_terms_toolbar_title, onBackClick = onBackClick) },
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White)
                    .padding(paddingValues),
        ) {
            terms.forEach { termsAgreement ->
                SettingNormalItem(
                    label = stringResource(id = termsAgreement.labelId),
                    onClick = { onTermsClick(termsAgreement) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingTermsScreenPreview() {
    val previewTerms =
        listOf(
            TermsUiModel(labelId = R.string.setting_terms_agree_service),
            TermsUiModel(labelId = R.string.setting_terms_agree_privacy),
            TermsUiModel(labelId = R.string.setting_terms_agree_health_connect),
        )
    MulkkamTheme {
        SettingTermsScreen(terms = previewTerms, onTermsClick = {}, onBackClick = {})
    }
}
