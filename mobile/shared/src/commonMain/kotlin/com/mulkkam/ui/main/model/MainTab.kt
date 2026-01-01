package com.mulkkam.ui.main.model

import com.mulkkam.ui.navigation.FriendsRoute
import com.mulkkam.ui.navigation.HistoryRoute
import com.mulkkam.ui.navigation.HomeRoute
import com.mulkkam.ui.navigation.SettingRoute
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.friends_title
import mulkkam.shared.generated.resources.ic_nav_friends
import mulkkam.shared.generated.resources.ic_nav_history
import mulkkam.shared.generated.resources.ic_nav_home
import mulkkam.shared.generated.resources.ic_nav_setting
import mulkkam.shared.generated.resources.main_history
import mulkkam.shared.generated.resources.main_home
import mulkkam.shared.generated.resources.main_setting
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class MainTab(
    val titleResource: StringResource,
    val iconResource: DrawableResource,
) {
    HOME(
        titleResource = Res.string.main_home,
        iconResource = Res.drawable.ic_nav_home,
    ),
    HISTORY(
        titleResource = Res.string.main_history,
        iconResource = Res.drawable.ic_nav_history,
    ),
    FRIENDS(
        titleResource = Res.string.friends_title,
        iconResource = Res.drawable.ic_nav_friends,
    ),
    SETTING(
        titleResource = Res.string.main_setting,
        iconResource = Res.drawable.ic_nav_setting,
    ),
    ;

    companion object {
        val DEFAULT: MainTab = HOME

        fun fromRoute(route: Any?): MainTab? =
            when (route) {
                is HomeRoute.Home -> HOME
                is HistoryRoute.History -> HISTORY
                is FriendsRoute.Friends -> FRIENDS
                is SettingRoute.Setting -> SETTING
                else -> null
            }
    }
}
