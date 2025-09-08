package com.mulkkam.ui.settingcups.dialog.model

import com.mulkkam.domain.model.cups.CupEmoji

data class CupEmojisUiModel(
    val cupEmojis: List<CupEmojiUiModel>,
) {
    val selectedCupEmojiId: Long?
        get() = cupEmojis.firstOrNull { it.isSelected }?.id

    val selectedCupEmojiUrl: String?
        get() = cupEmojis.firstOrNull { it.isSelected }?.cupEmojiUrl

    fun selectCupEmoji(cupEmojiId: Long): CupEmojisUiModel =
        copy(
            cupEmojis =
                cupEmojis.map { cupEmoji ->
                    if (cupEmoji.id == cupEmojiId) {
                        cupEmoji.copy(isSelected = true)
                    } else {
                        cupEmoji.copy(isSelected = false)
                    }
                },
        )
}

fun List<CupEmoji>.toUi(): CupEmojisUiModel =
    CupEmojisUiModel(
        cupEmojis = map { it.toUi() },
    )
