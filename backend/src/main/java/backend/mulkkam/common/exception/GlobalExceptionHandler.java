package backend.mulkkam.common.exception;

import backend.mulkkam.common.exception.errorCode.BadRequestErrorCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse<FailureBody> handleCommonException(HttpMessageNotReadableException e) {
        return ErrorResponse.from(BadRequestErrorCode.INVALID_ENUM_VALUE);
    }

    @ExceptionHandler(CommonException.class)
    public ErrorResponse<FailureBody> handleCommonException(CommonException e) {
        return ErrorResponse.from(e.getErrorCode());
    }
}
