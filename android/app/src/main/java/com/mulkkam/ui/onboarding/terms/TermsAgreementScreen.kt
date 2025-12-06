package com.mulkkam.ui.onboarding.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mulkkam.R
import com.mulkkam.ui.component.StyledText
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.onboarding.terms.component.TermsAgreementCheckBox
import com.mulkkam.ui.onboarding.terms.component.TermsAgreementItem
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun TermsAgreementScreen(
    navigateToBack: () -> Unit,
    loadToPage: (uri: Int) -> Unit,
    currentProgress: Int,
    viewModel: TermsAgreementViewModel = viewModel(),
) {
    val context = LocalContext.current

    val termsAgreements by viewModel.termsAgreements.collectAsStateWithLifecycle()
    val isAllChecked by viewModel.isAllChecked.collectAsStateWithLifecycle()
    val canNext by viewModel.canNext.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = navigateToBack,
                currentProgress = currentProgress,
            )
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp)
                    .background(White),
        ) {
            StyledText(
                fullText = stringResource(R.string.terms_agree_hint),
                highlightedTexts = listOf(stringResource(R.string.terms_agree_hint_highlight)),
                highlightStyle = MulKkamTheme.typography.title1,
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
                    text = stringResource(R.string.terms_agree_all),
                    style = MulKkamTheme.typography.title2,
                    color = Black,
                )
            }

            LazyColumn {
                items(
                    items = termsAgreements,
                    key = { item -> item.labelId },
                ) { item ->
                    val suffix =
                        if (item.isRequired) {
                            stringResource(R.string.terms_required_suffix)
                        } else {
                            stringResource(R.string.terms_optional_suffix)
                        }
                    TermsAgreementItem(
                        termsLabel = context.getString(item.labelId, suffix),
                        isChecked = item.isChecked,
                        onClickCheck = { viewModel.toggleCheckState(item) },
                        onClickNext = { loadToPage(item.uri) },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = { },
                modifier = Modifier.padding(),
                enabled = canNext,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsAgreementScreenPreview() {
    MulkkamTheme {
        TermsAgreementScreen(
            navigateToBack = {},
            loadToPage = {},
            currentProgress = 1,
        )
    }
}
