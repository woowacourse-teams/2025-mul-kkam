package com.mulkkam.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.ActivityNotificationBinding
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
        setUpBackPress()
    }

    private fun initObservers() {
        viewModel.notifications.observe(this) {
            adapter.changeItems(it)
        }

        viewModel.onApplySuggestion.observe(this) {
            Toast.makeText(this, R.string.home_notification_apply_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initClickListeners() {
        binding.ivBack.setSingleClickListener {
            val intent =
                Intent().apply {
                    putExtra(EXTRA_KEY_IS_APPLY, true)
                }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun setUpBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent =
                        Intent().apply {
                            putExtra(EXTRA_KEY_IS_APPLY, true)
                        }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            },
        )
    }

    companion object {
        const val EXTRA_KEY_IS_APPLY: String = "EXTRA_KEY_IS_APPLY"

        fun newIntent(context: Context): Intent = Intent(context, NotificationActivity::class.java)
    }
}
