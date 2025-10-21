package com.mulkkam.ui.settingcups.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemCupEmojiBinding
import com.mulkkam.ui.settingcups.model.CupEmojiUiModel
import com.mulkkam.ui.util.ImageShape
import com.mulkkam.ui.util.extensions.loadUrl
import com.mulkkam.ui.util.extensions.setSingleClickListener

class CupEmojiViewHolder(
    private val binding: ItemCupEmojiBinding,
    handler: Handler,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var item: CupEmojiUiModel

    init {
        binding.root.setSingleClickListener {
            handler.onSelectClick(item.id)
        }
    }

    fun bind(item: CupEmojiUiModel) {
        this.item = item

        with(binding) {
            ivEmoji.isSelected = item.isSelected
            ivEmoji.loadUrl(
                url = item.cupEmojiUrl,
                shape = ImageShape.Circle,
                placeholderRes = R.drawable.img_cup_placeholder,
            )
        }
    }

    fun interface Handler {
        fun onSelectClick(selectedId: Long)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            handler: Handler,
        ): CupEmojiViewHolder {
            val inflater = ItemCupEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CupEmojiViewHolder(inflater, handler)
        }
    }
}
