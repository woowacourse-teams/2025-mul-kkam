package com.mulkkam.ui.fixture

import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.intake.IntakeType
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

fun createFakeCupSmall(): Cup =
    Cup(
        id = 1L,
        name = CupName("종이컵"),
        amount = CupAmount(180),
        rank = 1,
        intakeType = IntakeType.WATER,
        emoji = "🥤",
    )

fun createFakeCupMedium(): Cup =
    Cup(
        id = 2L,
        name = CupName("스타벅스 톨"),
        amount = CupAmount(354),
        rank = 2,
        intakeType = IntakeType.WATER,
        emoji = "🧃",
    )

fun createFakeCupLarge(): Cup =
    Cup(
        id = 3L,
        name = CupName("스타벅스 그란데"),
        amount = CupAmount(473),
        rank = 3,
        intakeType = IntakeType.WATER,
        emoji = "🍵",
    )

fun createFakeCups(): Cups =
    Cups(
        listOf(
            createFakeCupSmall(),
            createFakeCupMedium(),
            createFakeCupLarge(),
        ),
    )
