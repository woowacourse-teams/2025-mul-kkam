package com.mulkkam.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.domain.DailyWaterIntake
import com.mulkkam.domain.WaterRecord
import com.mulkkam.domain.WaterRecords
import java.time.LocalDate
import java.time.LocalTime

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

    fun initWaterIntake() {
        _weeklyWaterIntake.value = WEEKLY_WATER_INTAKE
        _dailyWaterIntake.value = WEEKLY_WATER_INTAKE.first()
    }

    fun initWaterRecords() {
        waterRecords.addAll(WATER_RECORD)
    }

    fun updateDailyWaterIntake(dailyWaterIntake: DailyWaterIntake) {
        _dailyWaterIntake.value = dailyWaterIntake
    }

    companion object {
        val WEEKLY_WATER_INTAKE: List<DailyWaterIntake> =
            listOf(
                DailyWaterIntake(
                    1,
                    LocalDate.of(2025, 7, 21),
                    1200,
                    500,
                    10f,
                ),
                DailyWaterIntake(
                    2,
                    LocalDate.of(2025, 7, 22),
                    1200,
                    500,
                    20f,
                ),
                DailyWaterIntake(
                    3,
                    LocalDate.of(2025, 7, 23),
                    1200,
                    500,
                    30f,
                ),
                DailyWaterIntake(
                    4,
                    LocalDate.of(2025, 7, 24),
                    1200,
                    500,
                    40f,
                ),
                DailyWaterIntake(
                    5,
                    LocalDate.of(2025, 7, 25),
                    1200,
                    500,
                    50f,
                ),
                DailyWaterIntake(
                    6,
                    LocalDate.of(2025, 7, 26),
                    1200,
                    500,
                    60f,
                ),
                DailyWaterIntake(
                    7,
                    LocalDate.of(2025, 7, 27),
                    1200,
                    500,
                    70f,
                ),
            )

        val WATER_RECORD: List<WaterRecords> =
            listOf(
                WaterRecords(
                    LocalDate.of(2025, 7, 21),
                    listOf(
                        WaterRecord(
                            1,
                            LocalTime.now(),
                            100,
                        ),
                        WaterRecord(
                            1,
                            LocalTime.now(),
                            200,
                        ),
                    ),
                ),
                WaterRecords(
                    LocalDate.of(2025, 7, 22),
                    listOf(
                        WaterRecord(
                            2,
                            LocalTime.now(),
                            100,
                        ),
                        WaterRecord(
                            2,
                            LocalTime.now(),
                            100,
                        ),
                        WaterRecord(
                            2,
                            LocalTime.now(),
                            100,
                        ),
                    ),
                ),
                WaterRecords(
                    LocalDate.of(2025, 7, 23),
                    listOf(
                        WaterRecord(
                            3,
                            LocalTime.now(),
                            300,
                        ),
                    ),
                ),
                WaterRecords(
                    LocalDate.of(2025, 7, 24),
                    listOf(
                        WaterRecord(
                            4,
                            LocalTime.now(),
                            400,
                        ),
                        WaterRecord(
                            4,
                            LocalTime.now(),
                            400,
                        ),
                    ),
                ),
            )
    }
}
