package com.mulkkam.ui.settingaccountinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingAccountInfoActivity : ComponentActivity() {
    private val viewModel: SettingAccountInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MulkkamTheme {
                SettingAccountInfoRoute(
                    viewModel = viewModel,
                    navigateToBack = ::finish,
                    onNavigateToLogin = ::navigateToLogin,
                )
            }
        }
    }

    private fun navigateToLogin() {
        val intent =
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingAccountInfoActivity::class.java)
    }
}
