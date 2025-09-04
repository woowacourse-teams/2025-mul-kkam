package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum BadGateErrorCode implements ErrorCode {

    SEND_MESSAGE_FAILED,
    ;

    private static final HttpStatus status = HttpStatus.BAD_GATEWAY;

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
