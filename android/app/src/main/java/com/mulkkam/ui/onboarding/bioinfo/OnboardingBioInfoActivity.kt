package com.mulkkam.ui.onboarding.bioinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.onboarding.targetamount.OnboardingTargetAmountActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingBioInfoActivity : ComponentActivity() {
    private val viewModel: BioInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                BioInfoScreen(
                    navigateToBack = ::finish,
                    navigateToNextStep = {
                        startActivity(
                            OnboardingTargetAmountActivity.newIntent(
                                this,
                            ),
                        )
                    },
                    skipBioInfo = { startActivity(OnboardingTargetAmountActivity.newIntent(this)) },
                    currentProgress = CURRENT_PROGRESS,
                    viewModel = viewModel,
                )
            }
        }
    }

    companion object {
        private const val CURRENT_PROGRESS: Int = 3

        fun newIntent(context: Context): Intent = Intent(context, OnboardingBioInfoActivity::class.java)
    }
}
