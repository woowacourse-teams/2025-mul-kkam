package com.mulkkam.ui.settingterms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme

class SettingTermsActivity : ComponentActivity() {
    private val viewModel: SettingTermsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingTermsRoute(
                    viewModel = viewModel,
                    onBackClick = ::finish,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTermsActivity::class.java)
    }
}
