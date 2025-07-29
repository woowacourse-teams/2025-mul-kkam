package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.Cups
import com.mulkkam.domain.IntakeHistorySummary
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _todayIntakeHistorySummary = MutableLiveData<IntakeHistorySummary>()
    val todayIntakeHistorySummary: LiveData<IntakeHistorySummary> get() = _todayIntakeHistorySummary

    var cups: Cups? = null

    init {
        loadTodayIntakeHistorySummary()
        loadCups()
    }

    fun loadTodayIntakeHistorySummary() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val summary =
                RepositoryInjection.intakeRepository.getIntakeHistory(today, today).getByIndex(FIRST_INDEX)

            _todayIntakeHistorySummary.value = summary
        }
    }

    fun loadCups() {
        viewModelScope.launch {
            cups = RepositoryInjection.cupsRepository.getCups()
        }
    }

    fun addWaterIntake(cupRank: Int) {
        // TODO: 현재 cupRank가 2부터 들어가있음
        val cup = cups?.cups?.find { it.rank == cupRank }
        val cupAmount = cup?.amount

        viewModelScope.launch {
            RepositoryInjection.intakeRepository.postIntakeHistory(
                LocalDateTime.now(),
                cupAmount ?: DEFAULT_INTAKE_AMOUNT,
            )

            _todayIntakeHistorySummary.value =
                _todayIntakeHistorySummary.value?.copy(
                    totalIntakeAmount =
                        (
                            _todayIntakeHistorySummary.value?.totalIntakeAmount
                                ?: DEFAULT_INTAKE_AMOUNT
                        ) + (cupAmount ?: DEFAULT_INTAKE_AMOUNT),
                )
        }
    }

    companion object {
        private const val FIRST_INDEX: Int = 0
        private const val DEFAULT_INTAKE_AMOUNT: Int = 0
    }
}
