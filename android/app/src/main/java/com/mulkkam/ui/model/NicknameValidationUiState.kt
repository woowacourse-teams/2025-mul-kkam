package com.mulkkam.ui.model

enum class NicknameValidationUiState {
    /** 로컬 검증 통과 → 서버 검증 가능 상태 */
    PENDING_SERVER_VALIDATION,

    /** 서버까지 검증 완료 → 다음 단계 이동 가능 */
    VALID,

    /** 로컬 또는 서버 검증 실패 → 이동 불가능 */
    INVALID,
}
