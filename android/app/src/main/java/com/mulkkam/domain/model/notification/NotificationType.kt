package com.mulkkam.domain.model.notification

enum class NotificationType {
    SUGGESTION,
    REMIND,
    NOTICE,
    ;

    companion object {
        fun from(notificationType: String): NotificationType =
            when (notificationType) {
                SUGGESTION.name -> SUGGESTION
                REMIND.name -> REMIND
                NOTICE.name -> NOTICE
                else -> throw IllegalArgumentException()
            }
    }
}
