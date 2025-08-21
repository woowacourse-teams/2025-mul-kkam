package com.mulkkam.ui.settingcups.dialog.model

import com.mulkkam.domain.model.cups.CupEmoji

data class CupEmojiUiModel(
    val id: Long,
    val cupEmojiUrl: String,
    val isSelected: Boolean = false,
)

fun CupEmoji.toUi(): CupEmojiUiModel =
    CupEmojiUiModel(
        id = id,
        cupEmojiUrl = cupEmojiUrl,
    )
