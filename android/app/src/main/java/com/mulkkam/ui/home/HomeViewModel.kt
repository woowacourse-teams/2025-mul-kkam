package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.Cups
import com.mulkkam.domain.model.TodayProgressInfo
import com.mulkkam.ui.util.MutableSingleLiveData
import com.mulkkam.ui.util.SingleLiveData
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _todayProgressInfo = MutableLiveData<TodayProgressInfo>()
    val todayProgressInfo: LiveData<TodayProgressInfo> get() = _todayProgressInfo

    private val _cups: MutableLiveData<Cups> = MutableLiveData()
    val cups: LiveData<Cups> get() = _cups

    private val _characterChat: MutableLiveData<String> = MutableLiveData()
    val characterChat: LiveData<String> get() = _characterChat

    private val _alarmCount: MutableLiveData<Int> = MutableLiveData()
    val alarmCount: LiveData<Int> get() = _alarmCount

    private val _drinkSuccess: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val drinkSuccess: SingleLiveData<Unit> get() = _drinkSuccess

    init {
        loadTodayProgressInfo()
        loadCups()
        loadAlarmCount()
    }

    fun loadTodayProgressInfo() {
        viewModelScope.launch {
            val result = RepositoryInjection.membersRepository.getMembersProgressInfo(LocalDate.now())
            runCatching {
                val progressInfo = result.getOrError()
                _todayProgressInfo.value = progressInfo
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

    fun loadAlarmCount() {
        viewModelScope.launch {
            // TODO: 알림 개수 조회 API 연결
            // val result = RepositoryInjection.alarmRepository.getAlarmCount()
            runCatching {
                // val count = result.getOrError()
                _alarmCount.value = 2
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }

    fun addWaterIntakeByCup(cupId: Long) {
        val cup = cups.value?.findCupById(cupId) ?: return
        addWaterIntake(cup.amount)
    }

    fun addWaterIntake(amount: Int) {
        viewModelScope.launch {
            val result =
                RepositoryInjection.intakeRepository.postIntakeHistory(
                    LocalDateTime.now(),
                    amount,
                )
            runCatching {
                val intakeHistoryResult = result.getOrError()
                _todayProgressInfo.value =
                    todayProgressInfo.value?.updateProgressInfo(amount, intakeHistoryResult.achievementRate)
                _characterChat.value = intakeHistoryResult.comment
                _drinkSuccess.setValue(Unit)
            }.onFailure {
                // TODO: 에러 처리
            }
        }
    }
}
