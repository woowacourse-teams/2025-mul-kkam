package com.mulkkam.ui.onboarding.targetamount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingTargetAmountActivity : ComponentActivity() {
    private val viewModel: TargetAmountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: 이전 acitivity와 정보 연동 필요
        viewModel.loadRecommendedTargetAmount(
            nickname = "hwannow",
            gender = null,
            weight = null,
        )

        setContent {
            MulkkamTheme {
                TargetAmountScreen(
                    navigateToBack = ::finish,
                    navigateToNextStep = { },
                    currentProgress = CURRENT_PROGRESS,
                    hasBioInfo = false,
                )
            }
        }
    }

    companion object {
        private const val CURRENT_PROGRESS: Int = 4

        fun newIntent(context: Context): Intent = Intent(context, OnboardingTargetAmountActivity::class.java)
    }
}
