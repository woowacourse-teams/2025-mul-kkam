package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum BadRequestErrorCode implements ErrorCode {

    INVALID_ENUM_VALUE,
    INVALID_CUP_AMOUNT,
    INVALID_CUP_NICKNAME,
    INVALID_CUP_SIZE,
    INVALID_DATE_RANGE;

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
