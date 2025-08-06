package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum BadRequestErrorCode implements ErrorCode {

    INVALID_ENUM_VALUE,
    INVALID_CUP_AMOUNT,
    INVALID_CUP_NICKNAME,
    INVALID_DATE_RANGE,
    SAME_AS_BEFORE_NICKNAME,
    INVALID_CUP_COUNT,
    INVALID_CUP_RANK_VALUE,
    INVALID_MEMBER_NICKNAME,
    INVALID_FORECAST_TARGET_DATE,
    INVALID_FORECAST_DATE,
    INVALID_AMOUNT,
    MEMBER_ALREADY_EXIST_IN_OAUTH_ACCOUNT,
    INVALID_PAGE_SIZE_RANGE,
    INVALID_METHOD_ARGUMENT,
    ;

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
