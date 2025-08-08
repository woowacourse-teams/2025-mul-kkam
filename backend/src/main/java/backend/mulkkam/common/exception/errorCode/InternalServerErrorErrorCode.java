package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum InternalServerErrorErrorCode implements ErrorCode {

    INTER_SERVER_ERROR_CODE,
    ;


    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
