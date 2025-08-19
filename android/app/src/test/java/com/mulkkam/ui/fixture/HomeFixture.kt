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

// fun createFakeCups(): Cups {
//    val cup1 =
//        Cup(
//            id = 1L,
//            name = CupName("작은 컵"),
//            amount = CupAmount(250),
//            rank = 1,
//            intakeType = IntakeType.WATER,
//            emoji = "💧",
//        )
//    val cup2 =
//        Cup(
//            id = 2L,
//            name = CupName("큰 컵"),
//            amount = CupAmount(500),
//            rank = 2,
//            intakeType = IntakeType.WATER,
//            emoji = "🥤",
//        )
//    return Cups(cups = listOf(cup1, cup2))
// }
