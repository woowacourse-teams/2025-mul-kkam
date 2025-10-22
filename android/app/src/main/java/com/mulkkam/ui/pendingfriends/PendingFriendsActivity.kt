package com.mulkkam.ui.pendingfriends

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulkkamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingFriendsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                PendingFriendsScreen(
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
            "com.mulkkam.ui.pendingfriends.extra.IS_FRIEND_ACCEPTED"
        const val RESULT_CODE_FRIEND_ACCEPTED: Int = Activity.RESULT_OK

        fun newIntent(context: Context): Intent = Intent(context, PendingFriendsActivity::class.java)
    }
}
