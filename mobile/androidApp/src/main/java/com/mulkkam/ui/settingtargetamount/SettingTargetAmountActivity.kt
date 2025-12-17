package com.mulkkam.ui.settingtargetamount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingTargetAmountActivity : ComponentActivity() {
    private val viewModel: SettingTargetAmountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingTargetAmountScreen(
                    navigateToBack = ::finish,
                    viewModel = viewModel,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTargetAmountActivity::class.java)
    }
}
