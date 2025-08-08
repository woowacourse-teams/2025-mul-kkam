package com.mulkkam.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemHomeNotificationBinding
import com.mulkkam.domain.Alarm.NOTICE
import com.mulkkam.domain.Alarm.REMIND
import com.mulkkam.domain.Alarm.SUGGESTION
import com.mulkkam.domain.model.Notification

class NotificationViewHolder(
    private val binding: ItemHomeNotificationBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notification: Notification) {
        binding.tvNotificationDescription.text = notification.title
        binding.tvNotificationDateTime.text = notification.createdAt.toString()
        binding.viewNotReadAlarm.isVisible = !notification.isRead
        if (!notification.isRead) {
            binding.root.backgroundTintList = binding.root.context.getColorStateList(R.color.primary_10)
        }
        when (notification.type) {
            SUGGESTION -> {
                binding.tvSuggestion.isVisible = true
            }

            NOTICE -> {
            }

            REMIND -> {
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): NotificationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemHomeNotificationBinding.inflate(inflater, parent, false)
            return NotificationViewHolder(binding)
        }
    }
}
