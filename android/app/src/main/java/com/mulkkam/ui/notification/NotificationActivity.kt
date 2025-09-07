package com.mulkkam.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.mulkkam.R
import com.mulkkam.databinding.ActivityNotificationBinding
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.notification.adapter.NotificationAdapter
import com.mulkkam.ui.notification.adapter.NotificationItemTouchHelperCallback
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class NotificationActivity :
    BindingActivity<ActivityNotificationBinding>(
        ActivityNotificationBinding::inflate,
    ) {
    private val notificationAdapter: NotificationAdapter by lazy {
        NotificationAdapter(
            applyHandler = { amount, onComplete ->
                viewModel.applySuggestion(amount, onComplete)
            },
            deleteHandler = { id ->
                viewModel.deleteNotification(id)
            },
        )
    }
    private val itemTouchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(NotificationItemTouchHelperCallback(notificationAdapter))
    }
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRecyclerView()
        initObservers()
        initClickListeners()
        initBackPress()
    }

    private fun initRecyclerView() {
        binding.rvNotification.adapter = notificationAdapter
        binding.rvNotification.itemAnimator = null
        itemTouchHelper.attachToRecyclerView(binding.rvNotification)
    }

    private fun initObservers() {
        viewModel.notifications.observe(this) {
            handleNotificationUiState(it)
        }

        viewModel.applySuggestionUiState.observe(this) {
            if (it is MulKkamUiState.Success) {
                CustomToast
                    .makeText(this, getString(R.string.home_notification_apply_success))
                    .show()
            }
        }

        viewModel.deleteNotificationUiState.observe(this) {
            handleDeleteNotificationUiState(it)
        }
    }

    private fun handleNotificationUiState(notificationUiState: MulKkamUiState<List<Notification>>) {
        when (notificationUiState) {
            is MulKkamUiState.Success<List<Notification>> -> {
                notificationAdapter.changeItems(notificationUiState.data)
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

    private fun handleDeleteNotificationUiState(deleteNotificationUiState: MulKkamUiState<Unit>) {
        when (deleteNotificationUiState) {
            is MulKkamUiState.Success<Unit> -> {
                CustomToast
                    .makeText(this, getString(R.string.home_notification_delete_success))
                    .show()
            }
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Failure -> {
                CustomToast
                    .makeText(this, getString(R.string.home_notification_delete_failed))
                    .show()
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
