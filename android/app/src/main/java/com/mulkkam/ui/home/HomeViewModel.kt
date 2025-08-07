package com.mulkkam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.Cups
import com.mulkkam.domain.model.MembersProgressInfo
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val _todayProgressInfo = MutableLiveData<MembersProgressInfo>()
    val todayProgressInfo: LiveData<MembersProgressInfo> get() = _todayProgressInfo

    private val _cups: MutableLiveData<Cups> = MutableLiveData()
    val cups: LiveData<Cups> get() = _cups

    private val _characterChat: MutableLiveData<String> = MutableLiveData()
    val characterChat: LiveData<String> get() = _characterChat

    init {
        loadTodayProgressInfo()
        loadCups()
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
                _todayProgressInfo.value =
                    todayProgressInfo.value?.updateIntakeResult(cup.amount, intakeHistoryResult.achievementRate)
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
