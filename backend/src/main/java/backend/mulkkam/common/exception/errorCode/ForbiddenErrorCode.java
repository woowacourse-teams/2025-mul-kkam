package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum ForbiddenErrorCode implements ErrorCode {
    
    NO_PERMISSION_CUP,
    ;

    private static final HttpStatus status = HttpStatus.FORBIDDEN;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
