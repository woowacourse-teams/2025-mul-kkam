package backend.mulkkam.common.exception;

import org.springframework.http.HttpStatus;

public enum NotFoundErrorCode implements ErrorCode {

    NOT_FOUND_MEMBER,
    ;

    private static final HttpStatus status = HttpStatus.NOT_FOUND;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
