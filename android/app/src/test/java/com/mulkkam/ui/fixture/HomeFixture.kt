package com.mulkkam.ui.fixture

import com.mulkkam.domain.model.members.TodayProgressInfo

fun createFakeSuccessProgressInfo(): TodayProgressInfo =
    TodayProgressInfo(
        nickname = "테스터",
        streak = 5,
        achievementRate = 0.5f,
        targetAmount = 2000,
        totalAmount = 1000,
        comment = "절반이나 마셨어요!",
    )
