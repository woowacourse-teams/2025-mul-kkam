package com.mulkkam.ui.service

enum class NotificationAction {
    GO_HOME,
    GO_NOTIFICATION,
    FRIEND_REMINDER,
    UNKNOWN,
    ;

    companion object {
        fun from(action: String?): NotificationAction =
            when (action) {
                GO_HOME.name -> GO_HOME
                GO_NOTIFICATION.name -> GO_NOTIFICATION
                FRIEND_REMINDER.name -> FRIEND_REMINDER
                else -> UNKNOWN
            }
    }
}
