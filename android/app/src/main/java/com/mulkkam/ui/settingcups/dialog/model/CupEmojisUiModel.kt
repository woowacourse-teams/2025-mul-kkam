package com.mulkkam.ui.settingcups.dialog.model

import com.mulkkam.domain.model.cups.CupEmoji

data class CupEmojisUiModel(
    val cupEmojis: List<CupEmojiUiModel>,
) {
    val selectedCupEmojiId: Long?
        get() = cupEmojis.firstOrNull { it.isSelected }?.id

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
        cupEmojis =
            this.mapIndexed { index, cupEmoji ->
                if (index == 0) {
                    cupEmoji.toUi().copy(isSelected = true)
                } else {
                    cupEmoji.toUi()
                }
            },
    )
