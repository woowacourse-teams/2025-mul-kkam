package com.mulkkam.fixture

import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.intake.IntakeType

val HWANNOW_CUP =
    Cup(
        id = 1,
        name = CupName("hwannow"),
        amount = CupAmount(300),
        rank = 1,
        intakeType = IntakeType.WATER,
        emoji = "",
    )

val CHECHE_CUP =
    Cup(
        id = 2,
        name = CupName("cheche"),
        amount = CupAmount(200),
        rank = 2,
        intakeType = IntakeType.WATER,
        emoji = "",
    )

val KALI_CUP =
    Cup(
        id = 3,
        name = CupName("kali"),
        amount = CupAmount(500),
        rank = 3,
        intakeType = IntakeType.WATER,
        emoji = "",
    )
