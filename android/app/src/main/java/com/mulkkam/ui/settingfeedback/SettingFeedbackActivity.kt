package com.mulkkam.ui.settingfeedback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingFeedbackBinding
import com.mulkkam.di.LoggingInjection.mulKkamLogger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingFeedbackActivity : BindingActivity<ActivitySettingFeedbackBinding>(ActivitySettingFeedbackBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initHighlight()
        initClickListeners()
    }

    private fun initHighlight() {
        binding.tvFeedbackDescription.text =
            getString(R.string.setting_feedback_description).getAppearanceSpannable(
                this,
                R.style.title2,
                getString(R.string.setting_feedback_description_highlight),
            )
    }

    private fun initClickListeners() {
        binding.tvEmail.setSingleClickListener {
            mulKkamLogger.info(LogEvent.USER_ACTION, "Opened feedback email link")
            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    getString(R.string.feedback_email)
                        .toUri(),
                )
            startActivity(intent)
        }

        binding.ivBack.setSingleClickListener {
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingFeedbackActivity::class.java)
    }
}
