package backend.mulkkam.common.exception.errorCode;

import org.springframework.http.HttpStatus;

public enum InternalServerErrorErrorCode implements ErrorCode {

    INTER_SERVER_ERROR_CODE,
    NOT_EXIST_DEFAULT_CUP_EMOJI,
    ;


    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
