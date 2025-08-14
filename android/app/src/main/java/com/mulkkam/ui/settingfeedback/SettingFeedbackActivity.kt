package com.mulkkam.ui.settingfeedback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mulkkam.databinding.ActivitySettingFeedbackBinding
import com.mulkkam.ui.util.binding.BindingActivity

class SettingFeedbackActivity : BindingActivity<ActivitySettingFeedbackBinding>(ActivitySettingFeedbackBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingFeedbackActivity::class.java)
    }
}
