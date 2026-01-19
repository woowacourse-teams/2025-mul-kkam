package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.onboarding.terms.component.TermsAgreementCheckBox
import com.mulkkam.ui.onboarding.terms.component.TermsAgreementItem
import com.mulkkam.ui.onboarding.terms.model.TermsType
import com.mulkkam.ui.util.extensions.getStyledText
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.terms_agree_all
import mulkkam.shared.generated.resources.terms_agree_hint
import mulkkam.shared.generated.resources.terms_agree_hint_highlight
import mulkkam.shared.generated.resources.terms_optional_suffix
import mulkkam.shared.generated.resources.terms_required_suffix
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TermsScreen(
    padding: PaddingValues,
    navigateToBack: () -> Boolean,
    loadToPage: (uri: String) -> Unit,
    navigateToNextStep: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
    viewModel: TermsAgreementViewModel = koinViewModel(),
) {
    val termsAgreements by viewModel.termsAgreements.collectAsStateWithLifecycle()
    val isAllChecked by viewModel.isAllChecked.collectAsStateWithLifecycle()
    val canNext by viewModel.canNext.collectAsStateWithLifecycle()

    val onboardingInfo by remember(termsAgreements) {
        derivedStateOf {
            OnboardingInfo().copy(
                isMarketingNotificationAgreed =
                    termsAgreements.find { it.type == TermsType.MARKETING }?.isChecked == true,
                isNightNotificationAgreed =
                    termsAgreements.find { it.type == TermsType.NIGHT_NOTIFICATION }?.isChecked == true,
            )
        }
    }

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = { navigateToBack() },
                currentProgress = currentProgress,
            )
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .padding(padding),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp)
                    .background(White),
        ) {
            Text(
                text =
                    stringResource(resource = Res.string.terms_agree_hint).getStyledText(
                        highlightedText = arrayOf(stringResource(resource = Res.string.terms_agree_hint_highlight)),
                        style = MulKkamTheme.typography.title1,
                    ),
                style = MulKkamTheme.typography.body1,
                modifier = Modifier.padding(start = 8.dp),
            )

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.noRippleClickable(onClick = { viewModel.checkAllAgreement() }),
            ) {
                TermsAgreementCheckBox(
                    checked = isAllChecked,
                    onCheckedChange = { viewModel.checkAllAgreement() },
                )

                Text(
                    text = stringResource(resource = Res.string.terms_agree_all),
                    style = MulKkamTheme.typography.title2,
                    color = Black,
                )
            }

            LazyColumn {
                items(
                    items = termsAgreements,
                    key = { item -> item.type },
                ) { item ->
                    val suffix: String =
                        if (item.isRequired) {
                            stringResource(resource = Res.string.terms_required_suffix)
                        } else {
                            stringResource(resource = Res.string.terms_optional_suffix)
                        }
                    val (labelRes: StringResource, uri: String) =
                        item.type
                            .toResourceIds()
                            .let { it.first to stringResource(it.second) }
                    TermsAgreementItem(
                        termsLabel = stringResource(labelRes, suffix),
                        isChecked = item.isChecked,
                        onClickCheck = { viewModel.toggleCheckState(item) },
                        onClickNext = { loadToPage(uri) },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = { navigateToNextStep(onboardingInfo) },
                enabled = canNext,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsScreenPreview() {
    MulKkamTheme {
        TermsScreen(
            padding = PaddingValues(),
            navigateToBack = { true },
            loadToPage = { },
            navigateToNextStep = {},
            currentProgress = 1,
        )
    }
}
