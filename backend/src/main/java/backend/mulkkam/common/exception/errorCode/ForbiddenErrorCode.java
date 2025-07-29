package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum ForbiddenErrorCode implements ErrorCode {

    FORBIDDEN,
    ;

    private static final HttpStatus status = HttpStatus.FORBIDDEN;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
