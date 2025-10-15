package com.mulkkam.ui.searchfriends

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulkkamTheme

class SearchFriendsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SearchFriendsScreen(navigateToBack = { finish() })
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SearchFriendsActivity::class.java)
    }
}
