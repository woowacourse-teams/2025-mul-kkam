package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum ForbiddenErrorCode implements ErrorCode {

    FORBIDDEN,
    NOT_PERMITTED_FOR_CUP,
    ;

    private static final HttpStatus status = HttpStatus.FORBIDDEN;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
