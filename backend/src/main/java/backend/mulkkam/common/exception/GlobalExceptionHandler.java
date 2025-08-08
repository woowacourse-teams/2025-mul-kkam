package backend.mulkkam.common.exception;

import backend.mulkkam.common.exception.errorCode.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_ENUM_VALUE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_METHOD_ARGUMENT;
import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.INTER_SERVER_ERROR_CODE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse<FailureBody> handleInvalidEnum(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        logException(e, request, INVALID_ENUM_VALUE);
        return ErrorResponse.from(INVALID_ENUM_VALUE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse<FailureBody> handleInvalidMethodArgument(MethodArgumentNotValidException e,
                                                                  HttpServletRequest request) {
        logException(e, request, INVALID_METHOD_ARGUMENT);
        return ErrorResponse.from(INVALID_METHOD_ARGUMENT);
    }

    @ExceptionHandler(CommonException.class)
    public ErrorResponse<FailureBody> handleCommonException(CommonException e,
                                                            HttpServletRequest request) {
        logException(e, request, e.getErrorCode());
        return ErrorResponse.from(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse<FailureBody> handleUnexpectedException(
            Exception e,
            HttpServletRequest request
    ) {
        logException(e, request, INTER_SERVER_ERROR_CODE);
        return ErrorResponse.from(INTER_SERVER_ERROR_CODE);
    }

    private void logException(
            Exception e,
            HttpServletRequest request,
            ErrorCode errorCode
    ) {
        log.error("[Exception] code={}, status={}, exception={}, message={}, uri={}, method={}",
                errorCode.name(),
                errorCode.getStatus(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod()
        );
        log.debug("StackTrace: ", e);
    }
}
