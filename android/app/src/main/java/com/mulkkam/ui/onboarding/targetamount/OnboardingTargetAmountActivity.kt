package com.mulkkam.ui.onboarding.targetamount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.onboarding.cups.OnboardingCupsActivity
import com.mulkkam.ui.util.extensions.getSerializableCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingTargetAmountActivity : ComponentActivity() {
    private val viewModel: TargetAmountViewModel by viewModels()

    val onboardingInfo: OnboardingInfo? by lazy {
        intent.getSerializableCompat<OnboardingInfo>(KEY_ONBOARDING_INFO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadRecommendedTargetAmount(
            nickname = onboardingInfo?.nickname?.name ?: return,
            gender = onboardingInfo?.gender,
            weight = onboardingInfo?.weight,
        )

        setContent {
            MulkkamTheme {
                TargetAmountScreen(
                    navigateToBack = ::finish,
                    navigateToNextStep = { targetAmount ->
                        startActivity(
                            OnboardingCupsActivity.newIntent(
                                this,
                                onboardingInfo = onboardingInfo?.copy(targetAmount = targetAmount) ?: return@TargetAmountScreen,
                            ),
                        )
                    },
                    currentProgress = CURRENT_PROGRESS,
                    hasBioInfo = onboardingInfo?.hasBioInfo() ?: false,
                )
            }
        }
    }

    companion object {
        private const val KEY_ONBOARDING_INFO: String = "KEY_ONBOARDING_INFO"
        private const val CURRENT_PROGRESS: Int = 4

        fun newIntent(
            context: Context,
            onboardingInfo: OnboardingInfo?,
        ): Intent =
            Intent(context, OnboardingTargetAmountActivity::class.java).apply {
                onboardingInfo
                putExtra(KEY_ONBOARDING_INFO, onboardingInfo)
            }
    }
}
