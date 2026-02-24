package com.mulkkam.ui.setting.terms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.bio.HealthPlatform
import com.mulkkam.ui.settingterms.SettingTermsViewModel
import com.mulkkam.ui.settingterms.model.SettingTermsType
import com.mulkkam.ui.util.extensions.openLink
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.terms_privacy
import mulkkam.shared.generated.resources.terms_service
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TermsRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    viewModel: SettingTermsViewModel = koinViewModel(),
) {
    val healthManager = koinInject<HealthPlatform>()
    val coroutineScope = rememberCoroutineScope()
    val serviceLink = stringResource(Res.string.terms_service)
    val privacyLink = stringResource(Res.string.terms_privacy)
    val termsAgreements by viewModel.terms.collectAsStateWithLifecycle()

    TermsScreen(
        padding = padding,
        terms = termsAgreements,
        onTermsClick = { termsAgreement ->
            when (termsAgreement) {
                SettingTermsType.HEALTH_CONNECT -> {
                    coroutineScope.launch {
                        healthManager.navigateToHealthConnect()
                    }
                }

                SettingTermsType.SERVICE -> serviceLink.openLink()
                SettingTermsType.PRIVACY -> privacyLink.openLink()
            }
        },
        onBackClick = onNavigateToBack,
    )
}
