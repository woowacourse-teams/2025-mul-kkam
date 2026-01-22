package com.mulkkam.ui.main.model

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.mulkkam.ui.setting.SettingFragment

// TODO: HomeFragment was migrated to commonMain as pure Compose. Replace with Compose-based navigation.

enum class MainTab2(
    @IdRes val menuId: Int,
    val create: () -> Fragment,
) {
    // TODO: HOME tab needs to be updated to use pure Compose navigation instead of Fragment
    HOME(0, { Fragment() }), // Placeholder - HomeFragment migrated to commonMain
    HISTORY(0, { Fragment() }), // Placeholder - HistoryFragment migrated to commonMain
    FRIENDS(0, { Fragment() }),
    SETTING(0, { SettingFragment() }),
    ;

    companion object Companion {
        fun from(
            @IdRes id: Int,
        ): MainTab2? = entries.find { it.menuId == id }
    }
}
