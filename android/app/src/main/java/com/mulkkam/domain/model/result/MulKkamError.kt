package com.mulkkam.domain.model.result

sealed class MulKkamError : Throwable() {
    // 닉네임 관련 에러
    sealed class NicknameError : MulKkamError() {
        data object SameAsBefore : NicknameError() {
            private fun readResolve(): Any = SameAsBefore
        }

        data object InvalidNickname : NicknameError() {
            private fun readResolve(): Any = InvalidNickname
        }

        data object DuplicateNickname : NicknameError() {
            private fun readResolve(): Any = DuplicateNickname
        }

        object InvalidLength : NicknameError() {
            private fun readResolve(): Any = InvalidLength
        }

        object InvalidCharacters : NicknameError() {
            private fun readResolve(): Any = InvalidCharacters
        }
    }

    // 컵 설정 관련 에러
    sealed class SettingCupsError : MulKkamError() {
        data object InvalidCount : SettingCupsError() {
            private fun readResolve(): Any = InvalidCount
        }

        data object InvalidAmount : SettingCupsError() {
            private fun readResolve(): Any = InvalidAmount
        }

        data object InvalidNickname : SettingCupsError() {
            private fun readResolve(): Any = InvalidNickname
        }

        data object InvalidRankValue : SettingCupsError() {
            private fun readResolve(): Any = InvalidRankValue
        }
    }

    sealed class TargetAmountError : MulKkamError() {
        data object InvalidTargetAmount : TargetAmountError() {
            private fun readResolve(): Any = InvalidTargetAmount
        }
    }

    // 계정 관련 에러
    sealed class AccountError : MulKkamError() {
        data object NotExistUser : AccountError() {
            private fun readResolve(): Any = NotExistUser
        }

        data object InvalidToken : AccountError() {
            private fun readResolve(): Any = InvalidToken
        }
    }

    // 기록 관련 에러
    sealed class HistoryError : MulKkamError() {
        data object InvalidDateRange : HistoryError() {
            private fun readResolve(): Any = InvalidDateRange
        }
    }

    sealed class NotFoundError : MulKkamError() {
        data object Member : NotFoundError() {
            private fun readResolve(): Any = Member
        }

        data object Cup : NotFoundError() {
            private fun readResolve(): Any = Cup
        }

        data object IntakeType : NotFoundError() {
            private fun readResolve(): Any = IntakeType
        }
    }

    // 기타 공통 에러
    data object NetworkUnavailable : MulKkamError() {
        private fun readResolve(): Any = NetworkUnavailable
    }

    data object DatabaseError : MulKkamError() {
        private fun readResolve(): Any = DatabaseError
    }

    data object Unknown : MulKkamError() {
        private fun readResolve(): Any = Unknown
    }
}

fun Throwable.toMulKkamError(): MulKkamError =
    when (this) {
        is MulKkamError -> throw this
        else -> throw MulKkamError.Unknown
    }
