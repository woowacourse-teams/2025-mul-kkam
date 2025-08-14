package com.mulkkam.data.remote.model.error

import com.mulkkam.data.remote.model.error.ResponseError.AccountError
import com.mulkkam.data.remote.model.error.ResponseError.DatabaseError
import com.mulkkam.data.remote.model.error.ResponseError.HistoryError
import com.mulkkam.data.remote.model.error.ResponseError.NetworkUnavailable
import com.mulkkam.data.remote.model.error.ResponseError.NicknameError
import com.mulkkam.data.remote.model.error.ResponseError.NotFoundError
import com.mulkkam.data.remote.model.error.ResponseError.SettingCupsError
import com.mulkkam.data.remote.model.error.ResponseError.Unknown
import com.mulkkam.domain.model.result.MulKkamError
import java.net.ConnectException

sealed class ResponseError(
    val code: String,
) : Throwable() {
    // 닉네임 관련 에러
    sealed class NicknameError(
        code: String,
    ) : ResponseError(code) {
        object SameAsBefore : NicknameError("SAME_AS_BEFORE_NICKNAME") {
            private fun readResolve(): Any = SameAsBefore
        }

        object InvalidNickname : NicknameError("INVALID_MEMBER_NICKNAME") {
            private fun readResolve(): Any = InvalidNickname
        }

        object DuplicateNickname : NicknameError("DUPLICATE_MEMBER_NICKNAME") {
            private fun readResolve(): Any = DuplicateNickname
        }
    }

    // 컵 설정 관련 에러
    sealed class SettingCupsError(
        code: String,
    ) : ResponseError(code) {
        object InvalidCount : SettingCupsError("INVALID_CUP_COUNT") {
            private fun readResolve(): Any = InvalidCount
        }

        object InvalidAmount : SettingCupsError("INVALID_CUP_AMOUNT") {
            private fun readResolve(): Any = InvalidAmount
        }

        object InvalidNickname : SettingCupsError("INVALID_CUP_NICKNAME") {
            private fun readResolve(): Any = InvalidNickname
        }

        object InvalidRankValue : SettingCupsError("INVALID_CUP_RANK_VALUE") {
            private fun readResolve(): Any = InvalidRankValue
        }
    }

    // 계정 관련 에러
    sealed class AccountError(
        code: String,
    ) : ResponseError(code) {
        object NotExistUser : AccountError("NOT_EXIST_USER") {
            private fun readResolve(): Any = NotExistUser
        }

        object InvalidToken : AccountError("INVALID_TOKEN") {
            private fun readResolve(): Any = InvalidToken
        }

        data object Unauthorized : AccountError("Unauthorized") {
            private fun readResolve(): Any = Unauthorized
        }
    }

    // 기록 관련 에러
    sealed class HistoryError(
        code: String,
    ) : ResponseError(code) {
        object InvalidDateRange : HistoryError("INVALID_DATE_RANGE") {
            private fun readResolve(): Any = InvalidDateRange
        }
    }

    sealed class NotFoundError(
        code: String,
    ) : ResponseError(code) {
        object Member : NotFoundError("NOT_FOUND_MEMBER") {
            private fun readResolve(): Any = Member
        }

        object Cup : NotFoundError("NOT_FOUND_CUP") {
            private fun readResolve(): Any = Cup
        }

        object IntakeType : NotFoundError("NOT_FOUND_INTAKE_TYPE") {
            private fun readResolve(): Any = IntakeType
        }
    }

    // 기타 공통 에러
    object NetworkUnavailable : ResponseError("NETWORK_UNAVAILABLE") {
        private fun readResolve(): Any = NetworkUnavailable
    }

    object DatabaseError : ResponseError("DATABASE_ERROR") {
        private fun readResolve(): Any = DatabaseError
    }

    object Unknown : ResponseError("UNKNOWN") {
        private fun readResolve(): Any = Unknown
    }

    companion object {
        fun from(code: String?): ResponseError? =
            when (code) {
                // Nickname
                NicknameError.SameAsBefore.code -> NicknameError.SameAsBefore
                NicknameError.InvalidNickname.code -> NicknameError.InvalidNickname
                NicknameError.DuplicateNickname.code -> NicknameError.DuplicateNickname

                // SettingCups
                SettingCupsError.InvalidCount.code -> SettingCupsError.InvalidCount
                SettingCupsError.InvalidAmount.code -> SettingCupsError.InvalidAmount
                SettingCupsError.InvalidNickname.code -> SettingCupsError.InvalidNickname
                SettingCupsError.InvalidRankValue.code -> SettingCupsError.InvalidRankValue

                // Account
                AccountError.NotExistUser.code -> AccountError.NotExistUser
                AccountError.InvalidToken.code -> AccountError.InvalidToken

                // History
                HistoryError.InvalidDateRange.code -> HistoryError.InvalidDateRange

                // NotFound
                NotFoundError.Member.code -> NotFoundError.Member
                NotFoundError.Cup.code -> NotFoundError.Cup
                NotFoundError.IntakeType.code -> NotFoundError.IntakeType

                // Local
                NetworkUnavailable.code -> NetworkUnavailable
                DatabaseError.code -> DatabaseError

                null -> null
                else -> Unknown
            }
    }
}

fun ResponseError.toDomain(): MulKkamError =
    when (this) {
        // Nickname
        NicknameError.SameAsBefore -> MulKkamError.NicknameError.SameAsBefore
        NicknameError.InvalidNickname -> MulKkamError.NicknameError.InvalidNickname
        NicknameError.DuplicateNickname -> MulKkamError.NicknameError.DuplicateNickname

        // SettingCups
        SettingCupsError.InvalidCount -> MulKkamError.SettingCupsError.InvalidCount
        SettingCupsError.InvalidAmount -> MulKkamError.SettingCupsError.InvalidAmount
        SettingCupsError.InvalidNickname -> MulKkamError.SettingCupsError.InvalidNicknameLength
        SettingCupsError.InvalidRankValue -> MulKkamError.SettingCupsError.InvalidRankValue

        // Account
        AccountError.NotExistUser -> MulKkamError.AccountError.NotExistUser
        AccountError.InvalidToken -> MulKkamError.AccountError.InvalidToken

        // History
        HistoryError.InvalidDateRange -> MulKkamError.HistoryError.InvalidDateRange

        // NotFound
        NotFoundError.Member -> MulKkamError.NotFoundError.Member
        NotFoundError.Cup -> MulKkamError.NotFoundError.Cup
        NotFoundError.IntakeType -> MulKkamError.NotFoundError.IntakeType

        // Local
        NetworkUnavailable -> MulKkamError.NetworkUnavailable
        DatabaseError -> MulKkamError.DatabaseError

        else -> MulKkamError.Unknown
    }

fun Throwable.toResponseError(): ResponseError =
    when (this) {
        is ResponseError -> this

        is ConnectException -> NetworkUnavailable

        else -> Unknown
    }
