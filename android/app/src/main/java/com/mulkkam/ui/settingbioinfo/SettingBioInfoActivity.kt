package com.mulkkam.ui.settingbioinfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mulkkam.databinding.ActivitySettingBioInfoBinding
import com.mulkkam.ui.binding.BindingActivity

class SettingBioInfoActivity :
    BindingActivity<ActivitySettingBioInfoBinding>(
        ActivitySettingBioInfoBinding::inflate,
    ) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingBioInfoActivity::class.java)
    }
}
