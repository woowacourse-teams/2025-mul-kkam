package backend.mulkkam.common.exception;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_ENUM_VALUE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_METHOD_ARGUMENT;
import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.INTER_SERVER_ERROR_CODE;

import backend.mulkkam.common.exception.errorCode.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse<FailureBody> handleInvalidEnum(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        logCommonException(request, INVALID_ENUM_VALUE);
        return ErrorResponse.from(INVALID_ENUM_VALUE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse<FailureBody> handleInvalidMethodArgument(MethodArgumentNotValidException e,
                                                                  HttpServletRequest request) {
        logCommonException(request, INVALID_METHOD_ARGUMENT);
        return ErrorResponse.from(INVALID_METHOD_ARGUMENT);
    }

    @ExceptionHandler(CommonException.class)
    public ErrorResponse<FailureBody> handleCommonException(CommonException e,
                                                            HttpServletRequest request) {
        logCommonException(request, e.getErrorCode());
        return ErrorResponse.from(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse<FailureBody> handleUnexpectedException(
            Exception e,
            HttpServletRequest request
    ) {
        logInternalServerErrorException(e, request);
        return ErrorResponse.from(INTER_SERVER_ERROR_CODE);
    }

    private void logInternalServerErrorException(
            Exception e,
            HttpServletRequest request
    ) {
        request.setAttribute("errorLoggedByGlobal", true);

        String traceId = (String) request.getAttribute("traceId");

        log.error("[SERVER_ERROR] traceId = {}, code={}({} {}), message={}",
                traceId,
                INTER_SERVER_ERROR_CODE.name(),
                INTER_SERVER_ERROR_CODE.getStatus(),
                e.getClass().getSimpleName(),
                e.getMessage()
        );
        log.debug("StackTrace: ", e);
    }

    private void logCommonException(
            HttpServletRequest request,
            ErrorCode errorCode
    ) {
        request.setAttribute("errorLoggedByGlobal", true);

        String traceId = (String) request.getAttribute("traceId");

        log.info("[CLIENT_ERROR] traceId = {}, code={}({})",
                traceId,
                errorCode.name(),
                errorCode.getStatus()
        );
    }
}
