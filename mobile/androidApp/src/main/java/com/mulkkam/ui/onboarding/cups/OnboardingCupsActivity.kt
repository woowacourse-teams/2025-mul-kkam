package com.mulkkam.ui.onboarding.cups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.main.MainActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingCupsActivity : ComponentActivity() {
    private val viewModel: CupsViewModel by viewModel()

    private val onboardingInfo: OnboardingInfo? by lazy {
        intent.getStringExtra(KEY_ONBOARDING_INFO)?.let {
            Json.decodeFromString<OnboardingInfo>(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.updateOnboardingInfo(onboardingInfo = onboardingInfo ?: return)

        setContent {
            MulKkamTheme {
                CupsScreen(
                    navigateToBack = ::finish,
                    navigateToCoffeeEncyclopedia = { /* coffee encyclopedia activity migration completed */ },
                    currentProgress = CURRENT_PROGRESS,
                    viewModel = viewModel,
                    onCompleteOnboarding = {
                        startActivity(
                            MainActivity.newIntent(this).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            },
                        )
                    },
                )
            }
        }
    }

    companion object {
        private const val KEY_ONBOARDING_INFO: String = "KEY_ONBOARDING_INFO"
        private const val CURRENT_PROGRESS: Int = 5

        fun newIntent(
            context: Context,
            onboardingInfo: OnboardingInfo,
        ): Intent =
            Intent(context, OnboardingCupsActivity::class.java).apply {
                putExtra(KEY_ONBOARDING_INFO, Json.encodeToString(onboardingInfo))
            }
    }
}
