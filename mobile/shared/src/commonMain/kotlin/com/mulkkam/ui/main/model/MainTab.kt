package com.mulkkam.ui.main.model

import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_friends
import mulkkam.shared.generated.resources.ic_home
import mulkkam.shared.generated.resources.ic_record
import mulkkam.shared.generated.resources.ic_setting
import mulkkam.shared.generated.resources.main_friends
import mulkkam.shared.generated.resources.main_history
import mulkkam.shared.generated.resources.main_home
import mulkkam.shared.generated.resources.main_setting
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class MainTab(
    val iconRes: DrawableResource,
    val labelRes: StringResource,
) {
    HOME(Res.drawable.ic_home, Res.string.main_home),
    HISTORY(Res.drawable.ic_record, Res.string.main_history),
    FRIENDS(Res.drawable.ic_friends, Res.string.main_friends),
    SETTING(Res.drawable.ic_setting, Res.string.main_setting),
}
