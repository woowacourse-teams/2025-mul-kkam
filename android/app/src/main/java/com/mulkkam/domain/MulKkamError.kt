package com.mulkkam.domain

sealed class MulKkamError(
    val code: String,
) : Throwable() {
    // 닉네임 관련 에러
    sealed class NicknameError(
        code: String,
    ) : MulKkamError(code) {
        object TooShort : NicknameError("NICKNAME_TOO_SHORT") {
            private fun readResolve(): Any = TooShort
        }

        object TooLong : NicknameError("NICKNAME_TOO_LONG") {
            private fun readResolve(): Any = TooLong
        }

        object InvalidCharacter : NicknameError("NICKNAME_INVALID_CHAR") {
            private fun readResolve(): Any = InvalidCharacter
        }

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
    ) : MulKkamError(code) {
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
    ) : MulKkamError(code) {
        object NotExistUser : AccountError("NOT_EXIST_USER") {
            private fun readResolve(): Any = NotExistUser
        }

        object InvalidToken : AccountError("INVALID_TOKEN") {
            private fun readResolve(): Any = InvalidToken
        }
    }

    // 기록 관련 에러
    sealed class HistoryError(
        code: String,
    ) : MulKkamError(code) {
        object InvalidDateRange : HistoryError("INVALID_DATE_RANGE") {
            private fun readResolve(): Any = InvalidDateRange
        }
    }

    sealed class NotFoundError(
        code: String,
    ) : MulKkamError(code) {
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
    object NetworkUnavailable : MulKkamError("NETWORK_UNAVAILABLE") {
        private fun readResolve(): Any = NetworkUnavailable
    }

    object DatabaseError : MulKkamError("DATABASE_ERROR") {
        private fun readResolve(): Any = DatabaseError
    }

    object Unknown : MulKkamError("UNKNOWN") {
        private fun readResolve(): Any = Unknown
    }

    companion object {
        fun from(code: String?): MulKkamError? =
            when (code) {
                // Nickname
                NicknameError.TooShort.code -> NicknameError.TooShort
                NicknameError.TooLong.code -> NicknameError.TooLong
                NicknameError.InvalidCharacter.code -> NicknameError.InvalidCharacter
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
