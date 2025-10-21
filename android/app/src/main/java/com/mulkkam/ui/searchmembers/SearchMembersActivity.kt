package com.mulkkam.ui.searchmembers

import android.app.Activity
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
                SearchMembersScreen(
                    navigateToBack = { finish() },
                    onFriendAccepted = {
                        setResult(
                            RESULT_CODE_FRIEND_ACCEPTED,
                            Intent().putExtra(EXTRA_KEY_IS_FRIEND_ACCEPTED, true),
                        )
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_KEY_IS_FRIEND_ACCEPTED: String =
            "com.mulkkam.ui.searchmembers.extra.IS_FRIEND_ACCEPTED"
        const val RESULT_CODE_FRIEND_ACCEPTED: Int = Activity.RESULT_OK

        fun newIntent(context: Context): Intent = Intent(context, SearchMembersActivity::class.java)
    }
}
