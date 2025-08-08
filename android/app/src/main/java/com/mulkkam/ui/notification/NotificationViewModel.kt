package com.mulkkam.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.model.Notification
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationViewModel : ViewModel() {
    private val _notifications: MutableLiveData<List<Notification>> = MutableLiveData()
    val notifications: LiveData<List<Notification>> = _notifications

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val result =
                notificationRepository.getNotifications(
                    LocalDateTime.now(),
                    100,
                )
            runCatching {
                _notifications.value = result.getOrError()
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
