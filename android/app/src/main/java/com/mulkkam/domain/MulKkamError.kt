sealed class MulKkamError(
    val code: String,
) {
    // 닉네임 관련 에러
    sealed class NicknameError(
        code: String,
    ) : MulKkamError(code) {
        object TooShort : NicknameError("NICKNAME_TOO_SHORT")

        object TooLong : NicknameError("NICKNAME_TOO_LONG")

        object InvalidCharacter : NicknameError("NICKNAME_INVALID_CHAR")

        object SameAsBefore : NicknameError("SAME_AS_BEFORE_NICKNAME")

        object InvalidNickname : NicknameError("INVALID_MEMBER_NICKNAME")

        object DuplicateNickname : NicknameError("DUPLICATE_MEMBER_NICKNAME")
    }

    // 컵 설정 관련 에러
    sealed class SettingCupsError(
        code: String,
    ) : MulKkamError(code) {
        object InvalidCount : SettingCupsError("INVALID_CUP_COUNT")

        object InvalidAmount : SettingCupsError("INVALID_CUP_AMOUNT")

        object InvalidNickname : SettingCupsError("INVALID_CUP_NICKNAME")

        object InvalidRankValue : SettingCupsError("INVALID_CUP_RANK_VALUE")
    }

    // 계정 관련 에러
    sealed class AccountError(
        code: String,
    ) : MulKkamError(code) {
        object NotExistUser : AccountError("NOT_EXIST_USER")

        object InvalidToken : AccountError("INVALID_TOKEN")
    }

    // 기록 관련 에러
    sealed class HistoryError(
        code: String,
    ) : MulKkamError(code) {
        object InvalidDateRange : HistoryError("INVALID_DATE_RANGE")
    }

    sealed class NotFoundError(
        code: String,
    ) : MulKkamError(code) {
        object Member : NotFoundError("NOT_FOUND_MEMBER")

        object Cup : NotFoundError("NOT_FOUND_CUP")

        object IntakeType : NotFoundError("NOT_FOUND_INTAKE_TYPE")
    }

    // 기타 공통 에러
    object NetworkUnavailable : MulKkamError("NETWORK_UNAVAILABLE")

    object DatabaseError : MulKkamError("DATABASE_ERROR")

    object Unknown : MulKkamError("UNKNOWN")

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
