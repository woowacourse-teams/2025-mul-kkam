package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum ForbiddenErrorCode implements ErrorCode {

    NOT_PERMITTED_FOR_CUP,
    NOT_PERMITTED_FOR_INTAKE_HISTORY,
    NOT_PERMITTED_FOR_NOTIFICATION,
    NOT_PERMITTED_FOR_REMINDER_SCHEDULE,
    ;

    private static final HttpStatus status = HttpStatus.FORBIDDEN;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
