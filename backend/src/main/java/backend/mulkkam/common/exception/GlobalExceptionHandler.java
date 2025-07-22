package backend.mulkkam.common.exception;

import backend.mulkkam.common.exception.StandardResponse.FailureBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public StandardResponse<FailureBody> handleInvalidEnum(HttpMessageNotReadableException e) {
        return StandardResponse.failure(
                ErrorCode.BRO1.name(),
                HttpStatus.BAD_REQUEST
        );
    }
}
