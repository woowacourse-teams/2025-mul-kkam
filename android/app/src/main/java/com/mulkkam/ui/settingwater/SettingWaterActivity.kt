package com.mulkkam.ui.settingwater

import android.content.Context
import android.content.Intent
import com.mulkkam.databinding.ActivitySettingWaterBinding
import com.mulkkam.ui.binding.BindingActivity

class SettingWaterActivity : BindingActivity<ActivitySettingWaterBinding>(ActivitySettingWaterBinding::inflate) {
    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingWaterActivity::class.java)
    }
}
