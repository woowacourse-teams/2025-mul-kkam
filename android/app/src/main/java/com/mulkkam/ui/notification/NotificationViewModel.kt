package com.mulkkam.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationViewModel : ViewModel() {
    private val _notifications: MutableStateFlow<MulKkamUiState<List<Notification>>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val notifications: StateFlow<MulKkamUiState<List<Notification>>> = _notifications

    private val _applySuggestionUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(
            MulKkamUiState.Idle,
        )
    val applySuggestionUiState: StateFlow<MulKkamUiState<Unit>> = _applySuggestionUiState

    private val _deleteNotificationUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(
            MulKkamUiState.Idle,
        )
    val deleteNotificationUiState: StateFlow<MulKkamUiState<Unit>> = _deleteNotificationUiState

    private val _isApplySuggestion: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isApplySuggestion: StateFlow<Boolean> = _isApplySuggestion

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        if (notifications.value == MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _notifications.value = MulKkamUiState.Loading
                notificationRepository
                    .getNotifications(
                        LocalDateTime.now(),
                        NOTIFICATION_SIZE,
                    ).getOrError()
            }.onSuccess { notifications ->
                _notifications.value = MulKkamUiState.Success<List<Notification>>(notifications)
            }.onFailure {
                _notifications.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun applySuggestion(id: Int) {
        if (applySuggestionUiState.value == MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _applySuggestionUiState.value = MulKkamUiState.Loading
                notificationRepository.postSuggestionNotificationsApproval(id).getOrError()
            }.onSuccess {
                _applySuggestionUiState.value = MulKkamUiState.Success(Unit)
                _isApplySuggestion.value = true
            }.onFailure {
                _applySuggestionUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun deleteNotification(id: Int) {
        if (deleteNotificationUiState.value == MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _deleteNotificationUiState.value = MulKkamUiState.Loading
                notificationRepository.deleteNotifications(id).getOrError()
            }.onSuccess {
                _deleteNotificationUiState.value = MulKkamUiState.Success(Unit)
            }.onFailure {
                _deleteNotificationUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                loadNotifications()
            }
        }
    }

    companion object {
        private const val NOTIFICATION_SIZE: Int = 100
    }
}
