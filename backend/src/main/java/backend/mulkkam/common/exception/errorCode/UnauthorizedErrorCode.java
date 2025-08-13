package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum UnauthorizedErrorCode implements ErrorCode {

    INVALID_TOKEN,
    MISSING_AUTHORIZATION_HEADER,
    INVALID_AUTHORIZATION_HEADER,
    ;

    private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
