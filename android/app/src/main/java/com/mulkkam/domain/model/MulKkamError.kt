package com.mulkkam.domain.model

sealed class MulKkamError : Throwable() {
    // 닉네임 관련 에러
    sealed class NicknameError : MulKkamError() {
        object SameAsBefore : NicknameError() {
            private fun readResolve(): Any = SameAsBefore
        }

        object InvalidNickname : NicknameError() {
            private fun readResolve(): Any = InvalidNickname
        }

        object DuplicateNickname : NicknameError() {
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
        object InvalidCount : SettingCupsError() {
            private fun readResolve(): Any = InvalidCount
        }

        object InvalidAmount : SettingCupsError() {
            private fun readResolve(): Any = InvalidAmount
        }

        object InvalidNickname : SettingCupsError() {
            private fun readResolve(): Any = InvalidNickname
        }

        object InvalidRankValue : SettingCupsError() {
            private fun readResolve(): Any = InvalidRankValue
        }
    }

    sealed class TargetAmountError : MulKkamError() {
        object InvalidTargetAmount : TargetAmountError() {
            private fun readResolve(): Any = InvalidTargetAmount
        }
    }

    // 계정 관련 에러
    sealed class AccountError : MulKkamError() {
        object NotExistUser : AccountError() {
            private fun readResolve(): Any = NotExistUser
        }

        object InvalidToken : AccountError() {
            private fun readResolve(): Any = InvalidToken
        }
    }

    // 기록 관련 에러
    sealed class HistoryError : MulKkamError() {
        object InvalidDateRange : HistoryError() {
            private fun readResolve(): Any = InvalidDateRange
        }
    }

    sealed class NotFoundError : MulKkamError() {
        object Member : NotFoundError() {
            private fun readResolve(): Any = Member
        }

        object Cup : NotFoundError() {
            private fun readResolve(): Any = Cup
        }

        object IntakeType : NotFoundError() {
            private fun readResolve(): Any = IntakeType
        }
    }

    // 기타 공통 에러
    object NetworkUnavailable : MulKkamError() {
        private fun readResolve(): Any = NetworkUnavailable
    }

    object DatabaseError : MulKkamError() {
        private fun readResolve(): Any = DatabaseError
    }

    object Unknown : MulKkamError() {
        private fun readResolve(): Any = Unknown
    }
}
