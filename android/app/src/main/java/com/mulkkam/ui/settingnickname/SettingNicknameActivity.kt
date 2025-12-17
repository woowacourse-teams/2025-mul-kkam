package com.mulkkam.ui.settingnickname

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulkkamTheme

class SettingNicknameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingNicknameScreen(
                    navigateToBack = ::finish,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNicknameActivity::class.java)
    }
}
