package com.mulkkam.ui.widget

enum class IntakeWidgetAction {
    ACTION_DRINK,
    ACTION_REFRESH,
    ;

    companion object {
        fun from(action: String?): IntakeWidgetAction? = entries.firstOrNull { it.name == action }
    }
}
