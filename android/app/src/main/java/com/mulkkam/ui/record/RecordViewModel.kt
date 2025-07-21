package com.mulkkam.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.domain.DailyWaterIntake
import com.mulkkam.domain.WaterRecord
import com.mulkkam.domain.WaterRecords

class RecordViewModel : ViewModel() {
    private val _weeklyWaterIntake = MutableLiveData<List<DailyWaterIntake>>()
    val weeklyWaterIntake: LiveData<List<DailyWaterIntake>> get() = _weeklyWaterIntake

    private val _dailyWaterIntake = MutableLiveData<DailyWaterIntake>()
    val dailyWaterIntake: LiveData<DailyWaterIntake> get() = _dailyWaterIntake

    private val waterRecords: MutableList<WaterRecords> = mutableListOf()

    private val _dailyWaterRecords = MediatorLiveData<List<WaterRecord>>()
    val dailyWaterRecords: LiveData<List<WaterRecord>> get() = _dailyWaterRecords

    init {
        _dailyWaterRecords.addSource(_dailyWaterIntake) { intake ->
            _dailyWaterRecords.value = waterRecords.find { it.date == intake.date }?.waterRecords ?: emptyList()
        }
    }
}
