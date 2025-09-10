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
    INVALID_TARGET_AMOUNT,
    MEMBER_ALREADY_EXIST_IN_OAUTH_ACCOUNT,
    INVALID_PAGE_SIZE_RANGE,
    INVALID_METHOD_ARGUMENT,
    INVALID_DATE_FOR_DELETE_INTAKE_HISTORY,
    REFRESH_TOKEN_ALREADY_USED,
    REFRESH_TOKEN_IS_EXPIRED,
    INVALID_MEMBER_WEIGHT,
    INVALID_INTAKE_AMOUNT,
    NOT_ALL_MEMBER_CUPS_INCLUDED,
    ;

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
