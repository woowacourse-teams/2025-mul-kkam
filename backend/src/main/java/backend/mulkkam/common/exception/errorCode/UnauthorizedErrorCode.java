package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum UnauthorizedErrorCode implements ErrorCode {

    UNAUTHORIZED,
    MISSING_AUTHORIZATION_HEADER,
    INVALID_AUTHORIZATION_HEADER,
    INVALID_SECRET_KEY_FOR_NOTIFICATION,
    ;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
