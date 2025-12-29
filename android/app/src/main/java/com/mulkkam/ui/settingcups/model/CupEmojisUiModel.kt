package com.mulkkam.ui.settingcups.model

import com.mulkkam.domain.model.cups.CupEmoji

data class CupEmojisUiModel(
    val cupEmojis: List<CupEmojiUiModel>,
) {
    val selectedCupEmoji: CupEmojiUiModel?
        get() = cupEmojis.firstOrNull { it.isSelected }

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
