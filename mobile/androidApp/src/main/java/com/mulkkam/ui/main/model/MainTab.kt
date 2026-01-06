package com.mulkkam.ui.main.model

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.mulkkam.ui.friends.FriendsFragment
import com.mulkkam.ui.history.HistoryFragment
import com.mulkkam.ui.setting.SettingFragment

// TODO: HomeFragment was migrated to commonMain as pure Compose. Replace with Compose-based navigation.

enum class MainTab(
    @IdRes val menuId: Int,
    val create: () -> Fragment,
) {
    // TODO: HOME tab needs to be updated to use pure Compose navigation instead of Fragment
    HOME(0, { Fragment() }), // Placeholder - HomeFragment migrated to commonMain
    HISTORY(0, { HistoryFragment() }),
    FRIENDS(0, { FriendsFragment() }),
    SETTING(0, { SettingFragment() }),
    ;

    companion object {
        fun from(
            @IdRes id: Int,
        ): MainTab? = entries.find { it.menuId == id }
    }
}
