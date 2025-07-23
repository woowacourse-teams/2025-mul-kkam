package com.mulkkam.ui

import android.content.Context
import android.content.Intent
import com.mulkkam.databinding.ActivitySettingGoalBinding
import com.mulkkam.ui.binding.BindingActivity

class SettingGoalActivity : BindingActivity<ActivitySettingGoalBinding>(ActivitySettingGoalBinding::inflate) {
    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingGoalActivity::class.java)
    }
}
