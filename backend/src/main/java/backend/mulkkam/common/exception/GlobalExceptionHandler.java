package backend.mulkkam.common.exception;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_ENUM_VALUE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_METHOD_ARGUMENT;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse<FailureBody> handleInvalidEnum(HttpMessageNotReadableException e) {
        return ErrorResponse.from(INVALID_ENUM_VALUE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse<FailureBody> handleInvalidMethodArgument(MethodArgumentNotValidException e) {
        return ErrorResponse.from(INVALID_METHOD_ARGUMENT);
    }

    @ExceptionHandler(CommonException.class)
    public ErrorResponse<FailureBody> handleCommonException(CommonException e) {
        return ErrorResponse.from(e.getErrorCode());
    }
}
