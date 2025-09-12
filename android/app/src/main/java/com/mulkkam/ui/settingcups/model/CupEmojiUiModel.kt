package com.mulkkam.ui.settingcups.model

import android.os.Parcelable
import com.mulkkam.domain.model.cups.CupEmoji
import kotlinx.parcelize.Parcelize

@Parcelize
data class CupEmojiUiModel(
    val id: Long,
    val cupEmojiUrl: String,
    val isSelected: Boolean = false,
) : Parcelable {
    companion object {
        val EMPTY_CUP_EMOJI_UI_MODEL = CupEmojiUiModel(id = 0L, cupEmojiUrl = "", false)
    }
}

fun CupEmoji.toUi(): CupEmojiUiModel =
    CupEmojiUiModel(
        id = id,
        cupEmojiUrl = cupEmojiUrl,
    )

fun CupEmojiUiModel.toDomain(): CupEmoji =
    CupEmoji(
        id = id,
        cupEmojiUrl = cupEmojiUrl,
    )
