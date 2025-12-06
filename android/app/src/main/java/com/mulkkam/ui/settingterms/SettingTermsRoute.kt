package com.mulkkam.ui.settingterms

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import com.mulkkam.ui.util.extensions.openTermsLink

@Composable
fun SettingTermsRoute(
    viewModel: SettingTermsViewModel,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val healthConnectIntent = remember { Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS) }
    val termsAgreements: List<TermsUiModel> by viewModel.terms.collectAsStateWithLifecycle()

    SettingTermsScreen(
        terms = termsAgreements,
        onTermsClick = { termsAgreement ->
            when (termsAgreement.labelId) {
                R.string.setting_terms_agree_health_connect -> {
                    if (context.isHealthConnectAvailable()) {
                        context.startActivity(healthConnectIntent)
                    } else {
                        context.navigateToHealthConnectStore()
                    }
                }

                else -> context.openTermsLink(termsAgreement.uri)
            }
        },
        onBackClick = onBackClick,
    )
}
