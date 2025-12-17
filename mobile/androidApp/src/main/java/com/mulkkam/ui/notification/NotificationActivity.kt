package com.mulkkam.ui.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.R
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : ComponentActivity() {
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                NotificationScreen(
                    navigateToBack = { finishWithResult() },
                    viewModel = viewModel,
                )
            }
        }
        initBackPress()
        initObservers()
        clearNotifications()
    }

    private fun finishWithResult() {
        val isApply = viewModel.isApplySuggestion.value == true
        val intent =
            Intent().apply {
                putExtra(EXTRA_KEY_IS_APPLY, isApply)
            }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun initBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishWithResult()
                }
            },
        )
    }

    private fun initObservers() {
        viewModel.applySuggestionUiState.collectWithLifecycle(this) { applySuggestionUiState ->
            handleApplySuggestionUiState(applySuggestionUiState)
        }
    }

    private fun handleApplySuggestionUiState(applySuggestionUiState: MulKkamUiState<Unit>) {
        when (applySuggestionUiState) {
            is MulKkamUiState.Success -> {
                CustomToast
                    .makeText(
                        this@NotificationActivity,
                        getString(R.string.notification_apply_success),
                    ).show()
            }

            is MulKkamUiState.Idle, MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Failure -> {
                CustomToast
                    .makeText(
                        this@NotificationActivity,
                        getString(R.string.notification_apply_failed),
                    ).show()
            }
        }
    }

    private fun clearNotifications() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancelAll()
    }

    companion object {
        const val EXTRA_KEY_IS_APPLY: String = "EXTRA_KEY_IS_APPLY"

        fun newIntent(context: Context): Intent = Intent(context, NotificationActivity::class.java)
    }
}
