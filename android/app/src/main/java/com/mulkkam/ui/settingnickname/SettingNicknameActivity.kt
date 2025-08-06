package com.mulkkam.ui.settingnickname

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mulkkam.databinding.ActivitySettingNicknameBinding
import com.mulkkam.ui.binding.BindingActivity

class SettingNicknameActivity : BindingActivity<ActivitySettingNicknameBinding>(ActivitySettingNicknameBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNicknameActivity::class.java)
    }
}
