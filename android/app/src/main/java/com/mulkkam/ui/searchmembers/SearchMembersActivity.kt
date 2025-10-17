package com.mulkkam.ui.searchmembers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulkkamTheme

class SearchMembersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SearchMembersScreen(navigateToBack = { finish() })
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SearchMembersActivity::class.java)
    }
}
