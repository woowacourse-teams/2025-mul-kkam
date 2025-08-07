package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.domain.model.Cups
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _todayIntakeHistorySummary = MutableLiveData<IntakeHistorySummary>()
    val todayIntakeHistorySummary: LiveData<IntakeHistorySummary> get() = _todayIntakeHistorySummary

    private val _cups: MutableLiveData<Cups> = MutableLiveData()
    val cups: LiveData<Cups> get() = _cups

    private val _characterChat: MutableLiveData<String> = MutableLiveData()
    val characterChat: LiveData<String> get() = _characterChat

    init {
        loadTodayIntakeHistorySummary()
        loadCups()
    }

    fun loadTodayIntakeHistorySummary() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val result = RepositoryInjection.intakeRepository.getIntakeHistory(today, today)
            runCatching {
                val summary = result.getOrError().getByIndex(FIRST_INDEX)
                _todayIntakeHistorySummary.value = summary
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun loadCups() {
        viewModelScope.launch {
            val result = RepositoryInjection.cupsRepository.getCups()
            runCatching {
                _cups.value = result.getOrError()
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun addWaterIntake(cupId: Long) {
        val cup = cups.value?.findCupById(cupId) ?: return

        viewModelScope.launch {
            val result =
                RepositoryInjection.intakeRepository.postIntakeHistory(
                    LocalDateTime.now(),
                    cup.amount,
                )
            runCatching {
                val intakeHistoryResult = result.getOrError()
                _todayIntakeHistorySummary.value =
                    todayIntakeHistorySummary.value?.updateIntakeResult(cup.amount, intakeHistoryResult.achievementRate)
                _characterChat.value = intakeHistoryResult.comment
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    companion object {
        private const val FIRST_INDEX: Int = 0
    }
}
