package com.mulkkam.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.ActivityNotificationBinding
import com.mulkkam.ui.custom.toast.CustomToast
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
    }

    private fun initObservers() {
        viewModel.notifications.observe(this) {
            adapter.changeItems(it)
        }

        viewModel.onApplySuggestion.observe(this) {
            CustomToast.makeText(this, getString(R.string.home_notification_apply_success)).show()
        }
    }

    private fun initClickListeners() {
        binding.ivBack.setSingleClickListener {
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, NotificationActivity::class.java)
    }
}
