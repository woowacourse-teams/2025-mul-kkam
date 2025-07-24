package backend.mulkkam.common.exception;

import backend.mulkkam.common.exception.errorCode.ErrorCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final ErrorCode errorCode;

    public CommonException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
