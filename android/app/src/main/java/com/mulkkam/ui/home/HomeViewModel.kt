package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.Cups
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.domain.IntakeHistorySummary.Companion.EMPTY_DAILY_WATER_INTAKE
import kotlinx.coroutines.launch
import java.time.LocalDate

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
                RepositoryInjection.intakeRepository.getIntakeHistory(today, today).firstOrNull()

            _todayIntakeHistorySummary.value =
                summary ?: EMPTY_DAILY_WATER_INTAKE.copy(
                    targetAmount = RepositoryInjection.intakeRepository.getIntakeTarget(),
                )
        }
    }

    fun loadCups() {
        viewModelScope.launch {
            cups = RepositoryInjection.cupsRepository.getCups()
        }
    }

    fun addWaterIntake(cupRank: Int) {
        // TODO: 현재 cupRank가 2부터 들어가있음
        val cup = cups?.cups?.find { it.cupRank == cupRank }
        val cupAmount = cup?.cupAmount

        _todayIntakeHistorySummary.value =
            _todayIntakeHistorySummary.value?.copy(
                totalIntakeAmount =
                    (
                        _todayIntakeHistorySummary.value?.totalIntakeAmount
                            ?: DEFAULT_INTAKE_AMOUNT
                    ) + (cupAmount ?: DEFAULT_INTAKE_AMOUNT),
            )
    }

    companion object {
        private const val DEFAULT_INTAKE_AMOUNT = 0
    }
}
