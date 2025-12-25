package com.mulkkam.ui.onboarding.bioinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.onboarding.targetamount.OnboardingTargetAmountActivity
import kotlinx.serialization.json.Json

class OnboardingBioInfoActivity : ComponentActivity() {
    private val viewModel: BioInfoViewModel by viewModels()

    private val onboardingInfo: OnboardingInfo? by lazy {
        intent.getStringExtra(KEY_ONBOARDING_INFO)?.let {
            Json.decodeFromString<OnboardingInfo>(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
                BioInfoScreen(
                    navigateToBack = ::finish,
                    navigateToNextStep = { gender, bioWeight ->
                        startActivity(
                            OnboardingTargetAmountActivity.newIntent(
                                this,
                                onboardingInfo =
                                    onboardingInfo?.copy(
                                        gender = gender,
                                        weight = bioWeight,
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
                onboardingInfo?.let {
                    putExtra(KEY_ONBOARDING_INFO, Json.encodeToString(it))
                }
            }
    }
}
