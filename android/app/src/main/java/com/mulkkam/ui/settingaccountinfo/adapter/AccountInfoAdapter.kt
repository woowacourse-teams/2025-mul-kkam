package com.mulkkam.ui.settingaccountinfo.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.ui.settingaccountinfo.SettingAccountUiModel

class AccountInfoAdapter(
    private val handler: AccountInfoViewHolder.Handler,
) : RecyclerView.Adapter<AccountInfoViewHolder>() {
    private val userInfoItems: MutableList<SettingAccountUiModel> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AccountInfoViewHolder = AccountInfoViewHolder.from(parent, handler)

    override fun onBindViewHolder(
        holder: AccountInfoViewHolder,
        position: Int,
    ) {
        holder.bind(userInfoItems[position])
    }

    override fun getItemCount(): Int = userInfoItems.size

    fun submitList(newUserInfos: List<SettingAccountUiModel>) {
        userInfoItems.clear()
        userInfoItems.addAll(newUserInfos)

        if (userInfoItems.isEmpty() && newUserInfos.isNotEmpty()) {
            notifyItemRangeInserted(0, newUserInfos.size)
        } else {
            notifyItemRangeChanged(0, userInfoItems.size)
        }
    }
}
