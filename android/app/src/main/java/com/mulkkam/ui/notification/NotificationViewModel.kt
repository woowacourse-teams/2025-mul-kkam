package com.mulkkam.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection.intakeRepository
import com.mulkkam.di.RepositoryInjection.notificationRepository
import com.mulkkam.domain.model.Notification
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationViewModel : ViewModel() {
    private val _notifications: MutableLiveData<List<Notification>> = MutableLiveData()
    val notifications: LiveData<List<Notification>> = _notifications

    private val _onApplySuggestion: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val onApplySuggestion: SingleLiveData<Unit> = _onApplySuggestion

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val result =
                notificationRepository.getNotifications(
                    LocalDateTime.now(),
                    NOTIFICATION_SIZE,
                )
            runCatching {
                _notifications.value = result.getOrError()
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun applySuggestion(
        amount: Int,
        onComplete: (isSuccess: Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            val result = intakeRepository.patchIntakeAmountTargetSuggested(amount)
            runCatching {
                result.getOrError()
                _onApplySuggestion.setValue(Unit)
                onComplete(true)
            }.onFailure {
                // TODO: 에러 처리
                onComplete(false)
            }
        }
    }

    companion object {
        private const val NOTIFICATION_SIZE: Int = 100
    }
}
