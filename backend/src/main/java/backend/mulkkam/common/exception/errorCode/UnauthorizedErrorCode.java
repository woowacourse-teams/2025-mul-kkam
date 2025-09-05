package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum UnauthorizedErrorCode implements ErrorCode {

    UNAUTHORIZED,
    MISSING_AUTHORIZATION_HEADER,
    INVALID_AUTHORIZATION_HEADER,
    ONBOARDING_HAS_NOT_FINISHED,
    ;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
