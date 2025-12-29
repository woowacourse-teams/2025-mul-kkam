package com.mulkkam.ui.main.model

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.mulkkam.R
import com.mulkkam.ui.friends.FriendsFragment
import com.mulkkam.ui.history.HistoryFragment
import com.mulkkam.ui.home.HomeFragment
import com.mulkkam.ui.setting.SettingFragment

enum class MainTab(
    @IdRes val menuId: Int,
    val create: () -> Fragment,
) {
    HOME(R.id.item_home, { HomeFragment() }),
    HISTORY(R.id.item_history, { HistoryFragment() }),
    FRIENDS(R.id.item_friends, { FriendsFragment() }),
    SETTING(R.id.item_setting, { SettingFragment() }),
    ;

    companion object {
        fun from(
            @IdRes id: Int,
        ): MainTab? = entries.find { it.menuId == id }
    }
}
