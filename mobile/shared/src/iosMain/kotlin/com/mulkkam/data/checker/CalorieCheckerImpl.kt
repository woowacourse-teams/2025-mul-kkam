package com.mulkkam.data.checker

import com.mulkkam.domain.checker.CalorieChecker

// TODO: iOS HealthKit 연동 필요
class CalorieCheckerImpl : CalorieChecker {
    override fun checkCalorie(intervalHours: Long) {
        // iOS에서는 아직 지원하지 않음
    }
}
