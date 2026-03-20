package com.mulkkam.ui.setting.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.setting.setting.component.SettingNormalItem
import com.mulkkam.ui.setting.setting.component.SettingTopAppBar
import com.mulkkam.ui.settingterms.model.SettingTermsType
import com.mulkkam.ui.settingterms.model.toLabelResource
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_terms_toolbar_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TermsScreen(
    padding: PaddingValues,
    terms: List<SettingTermsType>,
    onTermsClick: (settingTermsType: SettingTermsType) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingTopAppBar(
                title = stringResource(Res.string.setting_terms_toolbar_title),
                onBackClick = onBackClick,
            )
        },
        containerColor = White,
        modifier =
            Modifier.fillMaxSize().background(White).padding(
                PaddingValues(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = 0.dp,
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding(),
                ),
            ),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White)
                    .padding(innerPadding),
        ) {
            terms.forEach { termsAgreement ->
                val label = stringResource(termsAgreement.toLabelResource())
                SettingNormalItem(
                    label = label,
                    onClick = { onTermsClick(termsAgreement) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsScreenPreview() {
    MulKkamTheme {
        TermsScreen(
            padding = PaddingValues(),
            terms = SettingTermsType.entries,
            onTermsClick = {},
            onBackClick = {},
        )
    }
}
