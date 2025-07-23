package com.mulkkam.ui

import android.content.Context
import android.content.Intent
import com.mulkkam.databinding.ActivitySettingProfileBinding
import com.mulkkam.ui.binding.BindingActivity

class SettingProfileActivity : BindingActivity<ActivitySettingProfileBinding>(ActivitySettingProfileBinding::inflate) {
    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingProfileActivity::class.java)
    }
}
