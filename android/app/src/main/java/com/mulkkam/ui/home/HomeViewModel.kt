package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.cups.Cups.Companion.EMPTY_CUPS
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.members.TodayProgressInfo.Companion.EMPTY_TODAY_PROGRESS_INFO
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _todayProgressInfoUiState: MutableLiveData<MulKkamUiState<TodayProgressInfo>> =
        MutableLiveData(MulKkamUiState.Success<TodayProgressInfo>(EMPTY_TODAY_PROGRESS_INFO))
    val todayProgressInfoUiState: LiveData<MulKkamUiState<TodayProgressInfo>> get() = _todayProgressInfoUiState

    private val _cupsUiState: MutableLiveData<MulKkamUiState<Cups>> = MutableLiveData(MulKkamUiState.Success<Cups>(EMPTY_CUPS))
    val cupsUiState: LiveData<MulKkamUiState<Cups>> get() = _cupsUiState

    private val _alarmCountUiState: MutableLiveData<MulKkamUiState<Int>> = MutableLiveData()
    val alarmCountUiState: LiveData<MulKkamUiState<Int>> get() = _alarmCountUiState

    private val _drinkUiState: MutableLiveData<MulKkamUiState<Int>> = MutableLiveData()
    val drinkUiState: LiveData<MulKkamUiState<Int>> get() = _drinkUiState

    init {
        loadTodayProgressInfo()
        loadCups()
        loadAlarmCount()
    }

    fun loadTodayProgressInfo() {
        viewModelScope.launch {
            runCatching {
                _todayProgressInfoUiState.value = MulKkamUiState.Loading
                RepositoryInjection.membersRepository.getMembersProgressInfo(LocalDate.now()).getOrError()
            }.onSuccess { todayProgressInfoUiState ->
                _todayProgressInfoUiState.value = MulKkamUiState.Success<TodayProgressInfo>(todayProgressInfoUiState)
            }.onFailure {
                _todayProgressInfoUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun loadCups() {
        viewModelScope.launch {
            runCatching {
                _cupsUiState.value = MulKkamUiState.Loading
                RepositoryInjection.cupsRepository.getCups().getOrError()
            }.onSuccess { cupsUiState ->
                _cupsUiState.value = MulKkamUiState.Success<Cups>(cupsUiState)
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Empty
            }
        }
    }

    fun loadAlarmCount() {
        viewModelScope.launch {
            // TODO: 알림 개수 조회 API 연결
            // RepositoryInjection.alarmRepository.getAlarmCount().getOrError()
            runCatching {
                _cupsUiState.value = MulKkamUiState.Loading
            }.onSuccess {
                _alarmCountUiState.value = MulKkamUiState.Success<Int>(2)
            }.onFailure {
                _alarmCountUiState.value = MulKkamUiState.Empty
            }
        }
    }

    fun addWaterIntakeByCup(cupId: Long) {
        val cups = (cupsUiState.value as? MulKkamUiState.Success<Cups>)?.data ?: return
        val cup = cups.findCupById(cupId) ?: return
        addWaterIntake(cup.amount)
    }

    fun addWaterIntake(amount: Int) {
        viewModelScope.launch {
            _drinkUiState.value = MulKkamUiState.Loading
            val result =
                RepositoryInjection.intakeRepository.postIntakeHistory(
                    LocalDateTime.now(),
                    amount,
                )
            runCatching {
                val intakeHistoryResult = result.getOrError()
                val todayProgressInfoUiState =
                    todayProgressInfoUiState.value as? MulKkamUiState.Success<TodayProgressInfo> ?: return@runCatching
                _todayProgressInfoUiState.value =
                    MulKkamUiState.Success<TodayProgressInfo>(
                        todayProgressInfoUiState.data.updateProgressInfo(
                            amountDelta = amount,
                            achievementRate = intakeHistoryResult.achievementRate,
                            comment = intakeHistoryResult.comment,
                        ),
                    )
                _drinkUiState.value = MulKkamUiState.Success<Int>(amount)
            }.onFailure {
                _drinkUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
