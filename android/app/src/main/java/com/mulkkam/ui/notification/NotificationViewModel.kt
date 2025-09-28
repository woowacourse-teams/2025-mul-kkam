package com.mulkkam.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationViewModel : ViewModel() {
    private val _notifications: MutableStateFlow<MulKkamUiState<List<Notification>>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val notifications: StateFlow<MulKkamUiState<List<Notification>>> = _notifications.asStateFlow()

    private val _applySuggestionUiState: MutableStateFlow<MulKkamUiState<Unit>> =
        MutableStateFlow(
            MulKkamUiState.Idle,
        )
    val applySuggestionUiState: StateFlow<MulKkamUiState<Unit>> =
        _applySuggestionUiState.asStateFlow()

    private val _isApplySuggestion: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isApplySuggestion: StateFlow<Boolean> = _isApplySuggestion.asStateFlow()

    private var nextCursor: Long? = null

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
            }.onSuccess { notificationsResult ->
                _notifications.value = MulKkamUiState.Success<List<Notification>>(notificationsResult.notifications)
                nextCursor = notificationsResult.nextCursor
            }.onFailure {
                _notifications.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun applySuggestion(id: Long) {
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

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            _notifications.value =
                MulKkamUiState.Success(
                    _notifications.value.toSuccessDataOrNull()?.filter { it.id != id }
                        ?: return@launch,
                )
            runCatching {
                notificationRepository.deleteNotifications(id).getOrError()
            }.onFailure {
                loadNotifications()
            }
        }
    }

    companion object {
        private const val NOTIFICATION_SIZE: Int = 100
    }
}
