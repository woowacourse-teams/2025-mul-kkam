package com.mulkkam.ui.settingterms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mulkkam.databinding.ActivitySettingNicknameBinding.inflate
import com.mulkkam.databinding.ActivitySettingTermsBinding
import com.mulkkam.ui.util.binding.BindingActivity

class SettingTermsActivity : BindingActivity<ActivitySettingTermsBinding>(ActivitySettingTermsBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTermsActivity::class.java)
    }
}
