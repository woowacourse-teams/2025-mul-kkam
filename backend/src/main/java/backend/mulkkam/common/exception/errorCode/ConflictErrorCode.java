package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum ConflictErrorCode implements ErrorCode {

    DUPLICATE_MEMBER_NICKNAME,
    ;

    private static final HttpStatus status = HttpStatus.CONFLICT;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
