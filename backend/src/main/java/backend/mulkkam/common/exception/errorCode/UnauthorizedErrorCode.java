package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum UnauthorizedErrorCode implements ErrorCode {

    UNAUTHORIZED,
    ;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
