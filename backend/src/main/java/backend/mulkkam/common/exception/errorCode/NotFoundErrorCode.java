package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum NotFoundErrorCode implements ErrorCode {

    NOT_FOUND_MEMBER,
    NOT_FOUND_CUP,
    NOT_FOUND_INTAKE_TYPE,
    NOT_FOUND_DEVICE,
    NOT_FOUND_TARGET_AMOUNT_SNAPSHOT,
    ;

    private static final HttpStatus status = HttpStatus.NOT_FOUND;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
