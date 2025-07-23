package backend.mulkkam.common.exception;

import org.springframework.http.HttpStatus;

public enum BadRequestErrorCode implements ErrorCode{

    INVALID_ENUM_VALUE,
    ;

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
