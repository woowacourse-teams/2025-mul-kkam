package com.mulkkam.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulkkam.databinding.ActivityNotificationBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.notification.adapter.NotificationAdapter

class NotificationActivity :
    BindingActivity<ActivityNotificationBinding>(
        ActivityNotificationBinding::inflate,
    ) {
    private val adapter: NotificationAdapter by lazy { NotificationAdapter() }
    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservers()
    }

    private fun initObservers() {
        viewModel.notifications.observe(this) {
            adapter.changeItems(it)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, NotificationActivity::class.java)
    }
}
