package com.mulkkam.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemHomeNotificationBinding
import com.mulkkam.domain.Alarm.SUGGESTION
import com.mulkkam.domain.model.Notification
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationViewHolder(
    private val binding: ItemHomeNotificationBinding,
    private val handler: NotificationHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notification: Notification) {
        binding.tvNotificationDescription.text = notification.title
        binding.tvNotificationDateTime.text = notification.createdAt.toRelativeTimeString()
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

    fun LocalDateTime.toRelativeTimeString(): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(this, now)

        return when {
            duration.toMinutes() < ONE_MINUTE -> getString(binding.root.context, R.string.home_notification_just_now)
            duration.toHours() < ONE_HOUR ->
                getString(
                    binding.root.context,
                    R.string.home_notification_minutes_ago,
                ).format(duration.toMinutes())
            duration.toDays() < ONE_DAY -> getString(binding.root.context, R.string.home_notification_hours_ago).format(duration.toHours())
            duration.toDays() < TWO_DAYS -> getString(binding.root.context, R.string.home_notification_one_day_ago)
            else -> this.format(dateTimeFormatter)
        }
    }

    fun interface NotificationHandler {
        fun onApply(amount: Int)
    }

    companion object {
        private const val ONE_MINUTE: Long = 1L
        private const val ONE_HOUR: Long = 1L
        private const val ONE_DAY: Long = 1L
        private const val TWO_DAYS: Long = 2L
        private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

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
