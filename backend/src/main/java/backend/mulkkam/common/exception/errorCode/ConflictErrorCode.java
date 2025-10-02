package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum ConflictErrorCode implements ErrorCode {

    DUPLICATE_MEMBER_NICKNAME,
    DUPLICATED_CUP,
    DUPLICATED_CUP_RANKS,
    REQUEST_CONFLICT,
    DUPLICATED_REMINDER_SCHEDULE,
    ;

    private static final HttpStatus status = HttpStatus.CONFLICT;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
