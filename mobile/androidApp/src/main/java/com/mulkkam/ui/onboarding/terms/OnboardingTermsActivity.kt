package com.mulkkam.ui.onboarding.terms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.onboarding.nickname.OnboardingNicknameActivity
import com.mulkkam.ui.terms.TermsAgreementViewModel
import com.mulkkam.ui.util.extensions.openTermsLink

class OnboardingTermsActivity : ComponentActivity() {
    private val viewModel: TermsAgreementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MulKkamTheme {
                TermsAgreementScreen(
                    navigateToBack = ::finish,
                    loadToPage = { openTermsLink(it) },
                    navigateToNextStep = { onboardingInfo ->
                        startActivity(OnboardingNicknameActivity.newIntent(this, onboardingInfo))
                    },
                    currentProgress = CURRENT_PROGRESS,
                    viewModel = viewModel,
                )
            }
        }
    }

    companion object {
        private const val CURRENT_PROGRESS: Int = 1

        fun newIntent(context: Context): Intent = Intent(context, OnboardingTermsActivity::class.java)
    }
}
