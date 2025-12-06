package com.mulkkam.ui.onboarding.nickname

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingNicknameActivity : ComponentActivity() {
    private val viewModel: NicknameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                NicknameScreen(
                    navigateToBack = ::finish,
                    navigateToNextStep = {},
                    currentProgress = CURRENT_PROGRESS,
                    viewModel = viewModel,
                )
            }
        }
    }

    companion object {
        private const val CURRENT_PROGRESS: Int = 2

        fun newIntent(context: Context): Intent = Intent(context, OnboardingNicknameActivity::class.java)
    }
}
