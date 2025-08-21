package com.mulkkam.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemHomeNotificationBinding
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.notification.NotificationType.NOTICE
import com.mulkkam.domain.model.notification.NotificationType.REMIND
import com.mulkkam.domain.model.notification.NotificationType.SUGGESTION
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.main.MainActivity.Companion.TOAST_BOTTOM_NAV_OFFSET
import com.mulkkam.ui.util.extensions.setSingleClickListener
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationViewHolder(
    private val binding: ItemHomeNotificationBinding,
    private val handler: NotificationApplyHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(notification: Notification) {
        setNotificationIcon(notification)
        setNotificationContent(notification)
        setReadStatus(notification)
        setSuggestion(notification)
    }

    private fun setNotificationIcon(notification: Notification) {
        val iconResId =
            when (notification.type) {
                SUGGESTION -> R.drawable.ic_notification_suggestion
                REMIND -> R.drawable.ic_notification_remind
                NOTICE -> R.drawable.ic_notification_notice
            }
        binding.ivNotificationType.setImageResource(iconResId)
    }

    private fun setNotificationContent(notification: Notification) {
        binding.tvNotificationTitle.text = notification.title
        binding.tvNotificationDateTime.text = notification.createdAt.toRelativeTimeString()
    }

    private fun setReadStatus(notification: Notification) {
        binding.viewUnreadAlarm.isVisible = !notification.isRead
        binding.root.backgroundTintList =
            binding.root.context.getColorStateList(
                if (notification.isRead) R.color.white else R.color.primary_10,
            )
    }

    private fun setSuggestion(notification: Notification) {
        val isSuggestable = notification.applyRecommendAmount == false
        binding.tvSuggestion.isVisible = isSuggestable
        if (isSuggestable) {
            binding.tvSuggestion.setSingleClickListener {
                handler.onApply(notification.id, ::onCompleteApply)
            }
        }
    }

    private fun LocalDateTime.toRelativeTimeString(): String {
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
                ).apply {
                    setGravityY(TOAST_BOTTOM_NAV_OFFSET)
                }.show()
        }
    }

    fun interface NotificationApplyHandler {
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
            handler: NotificationApplyHandler,
        ): NotificationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemHomeNotificationBinding.inflate(inflater, parent, false)
            return NotificationViewHolder(binding, handler)
        }
    }
}
