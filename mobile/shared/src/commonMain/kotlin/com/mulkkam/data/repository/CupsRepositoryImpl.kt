package com.mulkkam.data.repository

import com.mulkkam.data.remote.datasource.CupsRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.cups.NewCupRequest
import com.mulkkam.data.remote.model.request.cups.toData
import com.mulkkam.data.remote.model.response.cups.toDomain
import com.mulkkam.domain.model.CupEmoji
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.cups.Cups
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.CupsRepository

class CupsRepositoryImpl(
    private val cupsRemoteDataSource: CupsRemoteDataSource,
) : CupsRepository {
    override suspend fun getCups(): MulKkamResult<Cups> {
        val result = cupsRemoteDataSource.getCups()

        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getCupsDefault(): MulKkamResult<Cups> {
        val result = cupsRemoteDataSource.getCupsDefault()

        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postCup(
        name: CupName,
        amount: CupAmount,
        intakeType: IntakeType,
        emojiId: Long,
    ): MulKkamResult<Unit> {
        val result =
            cupsRemoteDataSource.postCup(
                NewCupRequest(
                    cupNickname = name.value,
                    cupAmount = amount.value,
                    intakeType = intakeType.name,
                    cupEmojiId = emojiId,
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun putCupsRank(cups: Cups): MulKkamResult<Cups> {
        val request = cups.toData()
        val result = cupsRemoteDataSource.putCupsRank(request)
        return result.fold(
            onSuccess = { MulKkamResult(data = cups) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun patchCup(
        id: Long,
        name: CupName,
        amount: CupAmount,
        intakeType: IntakeType,
        emojiId: Long,
    ): MulKkamResult<Unit> {
        val result =
            cupsRemoteDataSource.patchCup(
                cupId = id,
                newCupRequest =
                    NewCupRequest(
                        cupNickname = name.value,
                        cupAmount = amount.value,
                        intakeType = intakeType.name,
                        cupEmojiId = emojiId,
                    ),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteCup(id: Long): MulKkamResult<Unit> {
        val result = cupsRemoteDataSource.deleteCup(id)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getCupEmojis(): MulKkamResult<List<CupEmoji>> {
        val result = cupsRemoteDataSource.getCupEmojis()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun resetCups(): MulKkamResult<Unit> {
        val result = cupsRemoteDataSource.resetCups()
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
