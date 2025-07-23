package com.mulkkam.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mulkkam.databinding.ActivitySettingProfileBinding
import com.mulkkam.ui.binding.BindingActivity

class SettingProfileActivity : BindingActivity<ActivitySettingProfileBinding>(ActivitySettingProfileBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingProfileActivity::class.java)
    }
}
