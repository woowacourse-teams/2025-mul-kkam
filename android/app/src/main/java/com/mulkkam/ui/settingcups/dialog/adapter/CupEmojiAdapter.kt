package com.mulkkam.ui.settingcups.dialog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mulkkam.ui.settingcups.model.CupEmojiUiModel

class CupEmojiAdapter(
    private val handler: CupEmojiViewHolder.Handler,
) : ListAdapter<CupEmojiUiModel, CupEmojiViewHolder>(CupEmojiDiff) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CupEmojiViewHolder = CupEmojiViewHolder.from(parent, handler)

    override fun onBindViewHolder(
        holder: CupEmojiViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
