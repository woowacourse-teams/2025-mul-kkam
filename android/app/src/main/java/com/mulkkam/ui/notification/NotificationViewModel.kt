package com.mulkkam.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.model.notification.Notification
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationViewModel : ViewModel() {
    private val _notifications: MutableLiveData<MulKkamUiState<List<Notification>>> =
        MutableLiveData(MulKkamUiState.Idle)
    val notifications: LiveData<MulKkamUiState<List<Notification>>> = _notifications

    private val _applySuggestionUiState: MutableLiveData<MulKkamUiState<Unit>> =
        MutableLiveData(
            MulKkamUiState.Idle,
        )
    val applySuggestionUiState: LiveData<MulKkamUiState<Unit>> = _applySuggestionUiState

    private val _isApplySuggestion: MutableLiveData<Boolean> = MutableLiveData(false)
    val isApplySuggestion: LiveData<Boolean> = _isApplySuggestion

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

    fun applySuggestion(
        amount: Int,
        onComplete: (isSuccess: Boolean) -> Unit,
    ) {
        if (applySuggestionUiState.value == MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _applySuggestionUiState.value = MulKkamUiState.Loading
                intakeRepository.patchIntakeAmountTargetSuggested(amount).getOrError()
            }.onSuccess {
                _applySuggestionUiState.value = MulKkamUiState.Success(Unit)
                _isApplySuggestion.value = true
                onComplete(true)
            }.onFailure {
                _applySuggestionUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
                onComplete(false)
            }
        }
    }

    companion object {
        private const val NOTIFICATION_SIZE: Int = 100
    }
}
