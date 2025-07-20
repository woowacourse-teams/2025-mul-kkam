package com.mulkkam.ui.model

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.mulkkam.R
import com.mulkkam.ui.home.HomeFragment
import com.mulkkam.ui.record.RecordFragment
import com.mulkkam.ui.setting.SettingFragment

enum class MainTab(
    @IdRes val menuId: Int,
    val create: () -> Fragment,
) {
    HOME(R.id.item_home, { HomeFragment() }),
    RECORD(R.id.item_record, { RecordFragment() }),
    SETTING(R.id.item_setting, { SettingFragment() }),
    ;

    companion object {
        fun from(
            @IdRes id: Int,
        ): MainTab? = entries.find { it.menuId == id }
    }
}
