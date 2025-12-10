package com.mulkkam.ui.onboarding.cups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingCupsActivity : ComponentActivity() {
    private val viewModel: CupsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                CupsScreen(
                    navigateToBack = ::finish,
                    currentProgress = CURRENT_PROGRESS,
                    nickname = "hwannow", // TODO: data 연결
                    viewModel = viewModel,
                    onEditCup = {}, // TODO: show bottom sheet
                    onAddCup = {},
                )
            }
        }
    }

    companion object {
        private const val CURRENT_PROGRESS: Int = 5

        fun newIntent(context: Context): Intent = Intent(context, OnboardingCupsActivity::class.java)
    }
}
