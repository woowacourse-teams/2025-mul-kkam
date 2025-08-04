package com.mulkkam.ui.service

enum class NotificationAction {
    GO_HOME,
    GO_NOTIFICATION,
    UNKNOWN,
    ;

    companion object {
        fun from(action: String?): NotificationAction =
            when (action) {
                GO_HOME.name -> GO_HOME
                GO_NOTIFICATION.name -> GO_NOTIFICATION
                else -> UNKNOWN
            }
    }
}
