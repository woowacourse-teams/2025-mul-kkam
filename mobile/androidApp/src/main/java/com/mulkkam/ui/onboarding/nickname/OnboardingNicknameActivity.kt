package com.mulkkam.ui.onboarding.nickname

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.onboarding.bioinfo.OnboardingBioInfoActivity
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingNicknameActivity : ComponentActivity() {
    private val viewModel: NicknameViewModel by viewModel()

    private val onboardingInfo: OnboardingInfo? by lazy {
        intent.getStringExtra(KEY_ONBOARDING_INFO)?.let {
            Json.decodeFromString<OnboardingInfo>(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
                NicknameScreen(
                    navigateToBack = ::finish,
                    navigateToNextStep = { nickname ->
                        startActivity(
                            OnboardingBioInfoActivity.newIntent(
                                this,
                                onboardingInfo = onboardingInfo?.copy(nickname = nickname),
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
        private const val CURRENT_PROGRESS: Int = 2

        fun newIntent(
            context: Context,
            onboardingInfo: OnboardingInfo?,
        ): Intent =
            Intent(context, OnboardingNicknameActivity::class.java).apply {
                onboardingInfo?.let {
                    putExtra(KEY_ONBOARDING_INFO, Json.encodeToString(it))
                }
            }
    }
}
