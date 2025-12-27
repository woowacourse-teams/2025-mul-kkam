package com.mulkkam.ui.settingtargetamount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulKkamTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingTargetAmountActivity : ComponentActivity() {
    private val viewModel: SettingTargetAmountViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
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
