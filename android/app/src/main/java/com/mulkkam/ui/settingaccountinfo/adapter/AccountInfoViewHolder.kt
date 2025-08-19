package com.mulkkam.ui.settingaccountinfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.databinding.ItemSettingNormalBinding
import com.mulkkam.ui.settingaccountinfo.SettingAccountUiModel
import com.mulkkam.ui.util.extensions.setSingleClickListener

class AccountInfoViewHolder private constructor(
    private val binding: ItemSettingNormalBinding,
    private val handler: Handler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: SettingAccountUiModel) {
        binding.tvLabel.text = binding.root.context.getString(item.title)
        binding.root.setSingleClickListener { handler.onSettingNormalClick(item) }
    }

    interface Handler {
        fun onSettingNormalClick(item: SettingAccountUiModel)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            handler: Handler,
        ) = AccountInfoViewHolder(
            ItemSettingNormalBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            handler,
        )
    }
}
