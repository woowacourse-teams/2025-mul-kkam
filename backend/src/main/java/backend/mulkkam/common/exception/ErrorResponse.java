package backend.mulkkam.common.exception;

import backend.mulkkam.common.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponse<T> extends ResponseEntity<T> {

    private ErrorResponse(T body, HttpStatus status) {
        super(body, status);
    }

    public static ErrorResponse<FailureBody> from(ErrorCode errorCode) {
        return new ErrorResponse<>(
                new FailureBody(errorCode.name()),
                errorCode.getStatus()
        );
    }
}
