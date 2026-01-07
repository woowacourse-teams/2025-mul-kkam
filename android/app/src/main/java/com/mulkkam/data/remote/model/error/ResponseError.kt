package com.mulkkam.data.remote.model.error

import com.mulkkam.data.remote.model.error.ResponseError.AccountError
import com.mulkkam.data.remote.model.error.ResponseError.DatabaseError
import com.mulkkam.data.remote.model.error.ResponseError.FriendsError
import com.mulkkam.data.remote.model.error.ResponseError.HistoryError
import com.mulkkam.data.remote.model.error.ResponseError.NetworkUnavailable
import com.mulkkam.data.remote.model.error.ResponseError.NicknameError
import com.mulkkam.data.remote.model.error.ResponseError.NotFoundError
import com.mulkkam.data.remote.model.error.ResponseError.ReminderError
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
        data object SameAsBefore : NicknameError("SAME_AS_BEFORE_NICKNAME") {
            private fun readResolve(): Any = SameAsBefore
        }

        data object InvalidNickname : NicknameError("INVALID_MEMBER_NICKNAME") {
            private fun readResolve(): Any = InvalidNickname
        }

        data object DuplicateNickname : NicknameError("DUPLICATE_MEMBER_NICKNAME") {
            private fun readResolve(): Any = DuplicateNickname
        }
    }

    // 컵 설정 관련 에러
    sealed class SettingCupsError(
        code: String,
    ) : ResponseError(code) {
        data object InvalidCount : SettingCupsError("INVALID_CUP_COUNT") {
            private fun readResolve(): Any = InvalidCount
        }

        data object InvalidAmount : SettingCupsError("INVALID_CUP_AMOUNT") {
            private fun readResolve(): Any = InvalidAmount
        }

        data object InvalidNickname : SettingCupsError("INVALID_CUP_NICKNAME") {
            private fun readResolve(): Any = InvalidNickname
        }

        data object InvalidRankValue : SettingCupsError("INVALID_CUP_RANK_VALUE") {
            private fun readResolve(): Any = InvalidRankValue
        }
    }

    // 계정 관련 에러
    sealed class AccountError(
        code: String,
    ) : ResponseError(code) {
        data object NotExistUser : AccountError("NOT_EXIST_USER") {
            private fun readResolve(): Any = NotExistUser
        }

        data object InvalidToken : AccountError("INVALID_TOKEN") {
            private fun readResolve(): Any = InvalidToken
        }

        data object Unauthorized : AccountError("Unauthorized") {
            private fun readResolve(): Any = Unauthorized
        }

        data object RefreshTokenExpired : AccountError("REFRESH_TOKEN_IS_EXPIRED") {
            private fun readResolve(): Any = RefreshTokenExpired
        }

        data object RefreshTokenAlreadyUsed : AccountError("REFRESH_TOKEN_ALREADY_USED") {
            private fun readResolve(): Any = RefreshTokenAlreadyUsed
        }
    }

    // 기록 관련 에러
    sealed class HistoryError(
        code: String,
    ) : ResponseError(code) {
        data object InvalidDateRange : HistoryError("INVALID_DATE_RANGE") {
            private fun readResolve(): Any = InvalidDateRange
        }

        data object InvalidDateForDelete : HistoryError("INVALID_DATE_FOR_DELETE_INTAKE_HISTORY") {
            private fun readResolve(): Any = InvalidDateForDelete
        }
    }

    // 친구 관련 에러
    sealed class FriendsError(
        code: String,
    ) : ResponseError(code) {
        data object ReminderLimitExceeded : FriendsError("EXCEED_FRIEND_REMINDER_LIMIT") {
            private fun readResolve(): Any = ReminderLimitExceeded
        }
    }

    // 리마인더 관련 에러
    sealed class ReminderError(
        code: String,
    ) : ResponseError(code) {
        data object DuplicatedReminderSchedule : ReminderError("DUPLICATED_REMINDER_SCHEDULE") {
            private fun readResolve(): Any = DuplicatedReminderSchedule
        }
    }

    sealed class NotFoundError(
        code: String,
    ) : ResponseError(code) {
        data object Member : NotFoundError("NOT_FOUND_MEMBER") {
            private fun readResolve(): Any = Member
        }

        data object Friend : NotFoundError("NOT_FOUND_FRIEND") {
            private fun readResolve(): Any = Friend
        }

        data object Cup : NotFoundError("NOT_FOUND_CUP") {
            private fun readResolve(): Any = Cup
        }

        data object IntakeType : NotFoundError("NOT_FOUND_INTAKE_TYPE") {
            private fun readResolve(): Any = IntakeType
        }
    }

    // 기타 공통 에러
    data object NetworkUnavailable : ResponseError("NETWORK_UNAVAILABLE") {
        private fun readResolve(): Any = NetworkUnavailable
    }

    data object DatabaseError : ResponseError("DATABASE_ERROR") {
        private fun readResolve(): Any = DatabaseError
    }

    data object Unknown : ResponseError("UNKNOWN") {
        private fun readResolve(): Any = Unknown
    }

    companion object {
        fun from(code: String?): ResponseError =
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
                AccountError.Unauthorized.code -> AccountError.Unauthorized
                AccountError.RefreshTokenExpired.code -> AccountError.RefreshTokenExpired
                AccountError.RefreshTokenAlreadyUsed.code -> AccountError.RefreshTokenAlreadyUsed

                // History
                HistoryError.InvalidDateRange.code -> HistoryError.InvalidDateRange

                // Friends
                FriendsError.ReminderLimitExceeded.code -> FriendsError.ReminderLimitExceeded

                // Reminder
                ReminderError.DuplicatedReminderSchedule.code -> ReminderError.DuplicatedReminderSchedule

                // NotFound
                NotFoundError.Member.code -> NotFoundError.Member
                NotFoundError.Friend.code -> NotFoundError.Friend
                NotFoundError.Cup.code -> NotFoundError.Cup
                NotFoundError.IntakeType.code -> NotFoundError.IntakeType

                // Local
                NetworkUnavailable.code -> NetworkUnavailable
                DatabaseError.code -> DatabaseError

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
        AccountError.Unauthorized -> MulKkamError.AccountError.Unauthorized
        AccountError.RefreshTokenExpired -> MulKkamError.AccountError.RefreshTokenExpired
        AccountError.RefreshTokenAlreadyUsed -> MulKkamError.AccountError.RefreshTokenAlreadyUsed

        // History
        HistoryError.InvalidDateRange -> MulKkamError.HistoryError.InvalidDateRange
        HistoryError.InvalidDateForDelete -> MulKkamError.HistoryError.InvalidDateForDelete

        // Friends
        FriendsError.ReminderLimitExceeded -> MulKkamError.FriendsError.ReminderLimitExceeded

        // Reminder
        ReminderError.DuplicatedReminderSchedule -> MulKkamError.ReminderError.DuplicatedReminderSchedule

        // NotFound
        NotFoundError.Member -> MulKkamError.NotFoundError.Member
        NotFoundError.Friend -> MulKkamError.NotFoundError.Friend
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
