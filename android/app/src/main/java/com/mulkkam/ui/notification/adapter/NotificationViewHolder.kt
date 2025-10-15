package com.mulkkam.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemHomeNotificationBinding
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.notification.NotificationType.SUGGESTION
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.util.extensions.setSingleClickListener
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationViewHolder(
    private val binding: ItemHomeNotificationBinding,
    private val handler: NotificationHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notification: Notification) {
        with(binding) {
            if (notification.type == SUGGESTION) {
                ivNotificationType.setImageResource(R.drawable.ic_notification_sun)
            }
            tvNotificationTitle.text = notification.title
            tvNotificationDateTime.text = notification.createdAt.toRelativeTimeString()
            viewUnreadAlarm.isVisible = !notification.isRead
            root.backgroundTintList =
                if (!notification.isRead) {
                    root.context.getColorStateList(R.color.primary_10)
                } else {
                    root.context.getColorStateList(R.color.white)
                }

            tvSuggestion.isVisible = notification.type == SUGGESTION
            tvSuggestion.setSingleClickListener {
                handler.onApply(notification.recommendedTargetAmount, ::onCompleteApply)
            }
        }
    }

    fun LocalDateTime.toRelativeTimeString(): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(this, now)
        val context = binding.root.context

        return when {
            duration.toMinutes() < ONE_MINUTE ->
                getString(
                    context,
                    R.string.home_notification_just_now,
                )

            duration.toHours() < ONE_HOUR ->
                getString(
                    context,
                    R.string.home_notification_minutes_ago,
                ).format(duration.toMinutes())

            duration.toDays() < ONE_DAY ->
                getString(
                    context,
                    R.string.home_notification_hours_ago,
                ).format(duration.toHours())

            duration.toDays() < TWO_DAYS ->
                getString(
                    context,
                    R.string.home_notification_one_day_ago,
                )

            else -> this.format(dateTimeFormatter)
        }
    }

    private fun onCompleteApply(isSuccess: Boolean) {
        if (isSuccess) {
            binding.tvSuggestion.isVisible = false
        } else {
            CustomToast
                .makeText(
                    binding.root.context,
                    getString(binding.root.context, R.string.home_notification_apply_failed),
                ).show()
        }
    }

    fun interface NotificationHandler {
        fun onApply(
            amount: Int,
            onComplete: (isSuccess: Boolean) -> Unit,
        )
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
