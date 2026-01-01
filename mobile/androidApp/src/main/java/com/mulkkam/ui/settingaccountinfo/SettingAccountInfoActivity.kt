package com.mulkkam.ui.settingaccountinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulKkamTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingAccountInfoActivity : ComponentActivity() {
    private val viewModel: SettingAccountInfoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MulKkamTheme {
                SettingAccountInfoRoute(
                    viewModel = viewModel,
                    navigateToBack = ::finish,
                    onNavigateToLogin = ::navigateToLogin,
                )
            }
        }
    }

    // TODO: 추후 제거 필요
    private fun navigateToLogin() {
//        val intent =
//            Intent(this, LoginActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            }
//        startActivity(intent)
//        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingAccountInfoActivity::class.java)
    }
}
