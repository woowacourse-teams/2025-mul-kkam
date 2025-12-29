package com.mulkkam.ui.settingterms

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.settingterms.model.SettingTermsType
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import com.mulkkam.ui.util.extensions.openTermsLink

@Composable
fun SettingTermsRoute(
    viewModel: SettingTermsViewModel,
    navigateToBack: () -> Unit,
) {
    val context = LocalContext.current
    val healthConnectIntent = remember { Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS) }
    val termsAgreements: List<SettingTermsType> by viewModel.terms.collectAsStateWithLifecycle()

    SettingTermsScreen(
        terms = termsAgreements,
        onTermsClick = { termsAgreement ->
            when (termsAgreement) {
                SettingTermsType.HEALTH_CONNECT -> {
                    if (context.isHealthConnectAvailable()) {
                        context.startActivity(healthConnectIntent)
                    } else {
                        context.navigateToHealthConnectStore()
                    }
                }

                else -> {
                    val uriRes: Int? = termsAgreement.toUriResource()
                    if (uriRes != null) {
                        context.openTermsLink(uriRes)
                    }
                }
            }
        },
        onBackClick = navigateToBack,
    )
}
