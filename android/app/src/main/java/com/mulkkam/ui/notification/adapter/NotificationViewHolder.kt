package com.mulkkam.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemHomeNotificationBinding
import com.mulkkam.domain.Alarm.SUGGESTION
import com.mulkkam.domain.model.Notification

class NotificationViewHolder(
    private val binding: ItemHomeNotificationBinding,
    private val handler: NotificationHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notification: Notification) {
        binding.tvNotificationDescription.text = notification.title
        binding.tvNotificationDateTime.text = notification.createdAt.toString()
        binding.viewNotReadAlarm.isVisible = !notification.isRead
        if (!notification.isRead) {
            binding.root.backgroundTintList =
                binding.root.context.getColorStateList(R.color.primary_10)
        }

        binding.tvSuggestion.setOnClickListener {
            handler.onApply(notification.recommendedTargetAmount)
        }

        if (notification.type == SUGGESTION) {
            binding.tvSuggestion.isVisible = true
        }
    }

    fun interface NotificationHandler {
        fun onApply(amount: Int)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            handler: NotificationHandler,
        ): NotificationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemHomeNotificationBinding.inflate(inflater, parent, false)
            return NotificationViewHolder(binding, handler)
        }
    }
}
