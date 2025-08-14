package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.cups.CupCapacity
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.cups.Cups.Companion.EMPTY_CUPS
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.members.TodayProgressInfo.Companion.EMPTY_TODAY_PROGRESS_INFO
import com.mulkkam.domain.model.result.toMulKkamError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _todayProgressInfoUiState: MutableLiveData<MulKkamUiState<TodayProgressInfo>> =
        MutableLiveData(MulKkamUiState.Success<TodayProgressInfo>(EMPTY_TODAY_PROGRESS_INFO))
    val todayProgressInfoUiState: LiveData<MulKkamUiState<TodayProgressInfo>> get() = _todayProgressInfoUiState

    private val _cupsUiState: MutableLiveData<MulKkamUiState<Cups>> = MutableLiveData(MulKkamUiState.Success<Cups>(EMPTY_CUPS))
    val cupsUiState: LiveData<MulKkamUiState<Cups>> get() = _cupsUiState

    private val _alarmCountUiState: MutableLiveData<MulKkamUiState<Int>> = MutableLiveData(MulKkamUiState.Idle)
    val alarmCountUiState: LiveData<MulKkamUiState<Int>> get() = _alarmCountUiState

    private val _drinkUiState: MutableLiveData<MulKkamUiState<CupCapacity>> = MutableLiveData(MulKkamUiState.Idle)
    val drinkUiState: LiveData<MulKkamUiState<CupCapacity>> get() = _drinkUiState

    init {
        loadTodayProgressInfo()
        loadCups()
        loadAlarmCount()
    }

    fun loadTodayProgressInfo() {
        if (todayProgressInfoUiState.value is MulKkamUiState.Loading) return
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
        if (cupsUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _cupsUiState.value = MulKkamUiState.Loading
                RepositoryInjection.cupsRepository.getCups().getOrError()
            }.onSuccess { cupsUiState ->
                _cupsUiState.value = MulKkamUiState.Success<Cups>(cupsUiState)
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun loadAlarmCount() {
        if (alarmCountUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            // TODO: 알림 개수 조회 API 연결
            // RepositoryInjection.alarmRepository.getAlarmCount().getOrError()
            runCatching {
                _alarmCountUiState.value = MulKkamUiState.Loading
            }.onSuccess {
                _alarmCountUiState.value = MulKkamUiState.Success<Int>(2)
            }.onFailure {
                _cupsUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }

    fun addWaterIntakeByCup(cupId: Long) {
        val cups = cupsUiState.value?.toSuccessDataOrNull() ?: return
        val cup = cups.findCupById(cupId) ?: return
        addWaterIntake(cup.amount)
    }

    fun addWaterIntake(amount: Int) {
        if (drinkUiState.value is MulKkamUiState.Loading) return
        runCatching {
            CupCapacity(amount)
        }.onSuccess {
            addWaterIntake(it)
        }.onFailure {
            _drinkUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
        }
    }

    private fun addWaterIntake(amount: CupCapacity) {
        if (drinkUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _drinkUiState.value = MulKkamUiState.Loading
                RepositoryInjection.intakeRepository
                    .postIntakeHistory(LocalDateTime.now(), amount)
                    .getOrError()
            }.onSuccess { intakeHistory ->
                val current = todayProgressInfoUiState.value?.toSuccessDataOrNull() ?: return@launch
                _todayProgressInfoUiState.value =
                    MulKkamUiState.Success(
                        current.updateProgressInfo(
                            amountDelta = amount.value,
                            achievementRate = intakeHistory.achievementRate,
                            comment = intakeHistory.comment,
                        ),
                    )
                _drinkUiState.value = MulKkamUiState.Success(amount)
            }.onFailure {
                _drinkUiState.value = MulKkamUiState.Failure(it.toMulKkamError())
            }
        }
    }
}
