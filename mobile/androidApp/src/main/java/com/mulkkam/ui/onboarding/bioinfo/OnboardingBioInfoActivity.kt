package com.mulkkam.ui.onboarding.bioinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.onboarding.targetamount.OnboardingTargetAmountActivity
import com.mulkkam.ui.util.extensions.getSerializableCompat

class OnboardingBioInfoActivity : ComponentActivity() {
    private val viewModel: BioInfoViewModel by viewModels()

    private val onboardingInfo: OnboardingInfo? by lazy {
        intent.getSerializableCompat<OnboardingInfo>(KEY_ONBOARDING_INFO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
                BioInfoScreen(
                    navigateToBack = ::finish,
                    navigateToNextStep = { gender, weight ->
                        startActivity(
                            OnboardingTargetAmountActivity.newIntent(
                                this,
                                onboardingInfo =
                                    onboardingInfo?.copy(
                                        gender = gender,
                                        weight = weight,
                                    ),
                            ),
                        )
                    },
                    skipBioInfo = {
                        startActivity(
                            OnboardingTargetAmountActivity.newIntent(
                                this,
                                onboardingInfo = onboardingInfo,
                            ),
                        )
                    },
                    currentProgress = CURRENT_PROGRESS,
                    viewModel = viewModel,
                )
            }
        }
    }

    companion object {
        private const val KEY_ONBOARDING_INFO: String = "KEY_ONBOARDING_INFO"
        private const val CURRENT_PROGRESS: Int = 3

        fun newIntent(
            context: Context,
            onboardingInfo: OnboardingInfo?,
        ): Intent =
            Intent(context, OnboardingBioInfoActivity::class.java).apply {
                putExtra(KEY_ONBOARDING_INFO, onboardingInfo)
            }
    }
}
