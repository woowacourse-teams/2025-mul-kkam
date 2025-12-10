package com.mulkkam.ui.onboarding.nickname

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.onboarding.bioinfo.OnboardingBioInfoActivity
import com.mulkkam.ui.util.extensions.getSerializableCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingNicknameActivity : ComponentActivity() {
    private val viewModel: NicknameViewModel by viewModels()

    val onboardingInfo: OnboardingInfo? by lazy {
        intent.getSerializableCompat<OnboardingInfo>(KEY_ONBOARDING_INFO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
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
                putExtra(KEY_ONBOARDING_INFO, onboardingInfo)
            }
    }
}
