package backend.mulkkam.common.exception;

import backend.mulkkam.common.exception.errorCode.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static backend.mulkkam.common.exception.errorCode.ErrorCode.INVALID_ENUM_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<FailureBody> handleInvalidEnum(HttpMessageNotReadableException e) {
        return ResponseEntity.ok(new FailureBody(INVALID_ENUM_VALUE.name()));
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<FailureBody> handleCommonExceptionTemp(CommonException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(new FailureBody(errorCode.name()));
    }
}
