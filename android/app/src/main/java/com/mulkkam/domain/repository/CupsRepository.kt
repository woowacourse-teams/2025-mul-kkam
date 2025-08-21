package com.mulkkam.domain.repository

import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupEmoji
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.result.MulKkamResult

interface CupsRepository {
    suspend fun getCups(): MulKkamResult<Cups>

    suspend fun postCup(
        name: CupName,
        amount: CupAmount,
        intakeType: IntakeType,
        emojiId: Long,
    ): MulKkamResult<Unit>

    suspend fun putCupsRank(cups: Cups): MulKkamResult<Cups>

    suspend fun patchCup(
        id: Long,
        name: CupName,
        amount: CupAmount,
        intakeType: IntakeType,
        emojiId: Long,
    ): MulKkamResult<Unit>

    suspend fun deleteCup(id: Long): MulKkamResult<Unit>

    suspend fun getCupEmojis(): MulKkamResult<List<CupEmoji>>
}
