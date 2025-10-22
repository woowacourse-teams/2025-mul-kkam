package com.mulkkam.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.domain.repository.NotificationRepository
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
        private val logger: Logger,
    ) : ViewModel() {
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

        private val _loadUiState: MutableStateFlow<MulKkamUiState<Unit>> =
            MutableStateFlow(MulKkamUiState.Idle)
        val loadUiState: StateFlow<MulKkamUiState<Unit>> = _loadUiState.asStateFlow()

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
                    _notifications.value =
                        MulKkamUiState.Success<List<Notification>>(notificationsResult.notifications)
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
                    logger.info(LogEvent.PUSH_NOTIFICATION, "Applying suggestion notification id=$id")
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
                    logger.info(LogEvent.PUSH_NOTIFICATION, "Deleting notification id=$id")
                    notificationRepository.deleteNotifications(id).getOrError()
                }.onFailure {
                    loadNotifications()
                }
            }
        }

        fun loadMore() {
            if (nextCursor == null) return
            viewModelScope.launch {
                runCatching {
                    _loadUiState.value = MulKkamUiState.Loading
                    notificationRepository
                        .getNotifications(
                            time = LocalDateTime.now(),
                            size = NOTIFICATION_SIZE,
                            lastId = nextCursor,
                        ).getOrError()
                }.onSuccess { notificationsResult ->
                    _loadUiState.value = MulKkamUiState.Success(Unit)
                    val currentList = notifications.value.toSuccessDataOrNull() ?: emptyList()
                    val updatedList = currentList + notificationsResult.notifications
                    _notifications.value = MulKkamUiState.Success(updatedList)
                    nextCursor = notificationsResult.nextCursor
                }.onFailure {
                    _loadUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                }
            }
        }

        companion object {
            private const val NOTIFICATION_SIZE: Int = 20
        }
    }
