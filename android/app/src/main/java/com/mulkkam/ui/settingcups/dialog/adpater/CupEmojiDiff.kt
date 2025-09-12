package com.mulkkam.ui.settingcups.dialog.adpater

import androidx.recyclerview.widget.DiffUtil
import com.mulkkam.ui.settingcups.model.CupEmojiUiModel

object CupEmojiDiff : DiffUtil.ItemCallback<CupEmojiUiModel>() {
    override fun areItemsTheSame(
        oldItem: CupEmojiUiModel,
        newItem: CupEmojiUiModel,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: CupEmojiUiModel,
        newItem: CupEmojiUiModel,
    ): Boolean = oldItem == newItem
}
