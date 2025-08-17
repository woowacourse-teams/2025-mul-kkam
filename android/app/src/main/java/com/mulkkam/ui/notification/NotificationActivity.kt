package com.mulkkam.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.ActivityNotificationBinding
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.notification.adapter.NotificationAdapter
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class NotificationActivity :
    BindingActivity<ActivityNotificationBinding>(
        ActivityNotificationBinding::inflate,
    ) {
    private val adapter: NotificationAdapter by lazy {
        NotificationAdapter { amount, onComplete ->
            viewModel.applySuggestion(amount, onComplete)
        }
    }
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvNotification.adapter = adapter

        initObservers()
        initClickListeners()
        initBackPress()
    }

    private fun initObservers() {
        viewModel.notifications.observe(this) {
            handleNotificationUiState(it)
        }

        viewModel.applySuggestionUiState.observe(this) {
            if (it is MulKkamUiState.Success) {
                Toast
                    .makeText(this, R.string.home_notification_apply_success, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handleNotificationUiState(notificationUiState: MulKkamUiState<List<Notification>>) {
        when (notificationUiState) {
            is MulKkamUiState.Success<List<Notification>> -> {
                adapter.changeItems(notificationUiState.data)
                binding.includeNotificationShimmer.root.visibility = GONE
            }
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Loading -> binding.includeNotificationShimmer.root.visibility = VISIBLE
            is MulKkamUiState.Failure -> {
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(R.string.load_info_error),
                        R.drawable.ic_alert_circle,
                    ).show()
            }
        }
    }

    private fun initClickListeners() {
        binding.ivBack.setSingleClickListener {
            finishWithResult()
        }
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

    companion object {
        const val EXTRA_KEY_IS_APPLY: String = "EXTRA_KEY_IS_APPLY"

        fun newIntent(context: Context): Intent = Intent(context, NotificationActivity::class.java)
    }
}
