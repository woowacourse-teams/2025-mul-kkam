package backend.mulkkam.common.exception;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse<FailureBody> handleInvalidEnum(HttpMessageNotReadableException e) {
        return ErrorResponse.from(BadRequestErrorCode.INVALID_ENUM_VALUE);
    }

    @ExceptionHandler(CommonException.class)
    public ErrorResponse<FailureBody> handleInvalidEnum(CommonException e) {
        return ErrorResponse.from(e.getErrorCode());
    }
}
