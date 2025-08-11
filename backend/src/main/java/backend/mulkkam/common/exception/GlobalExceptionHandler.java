package backend.mulkkam.common.exception;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_ENUM_VALUE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_METHOD_ARGUMENT;
import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.INTER_SERVER_ERROR_CODE;

import backend.mulkkam.common.exception.errorCode.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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

        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("type", "SERVER_ERROR");
        logMap.put("trace_id", traceId);
        logMap.put("error_code", INTER_SERVER_ERROR_CODE.name());
        logMap.put("status", INTER_SERVER_ERROR_CODE.getStatus());
        logMap.put("error_class", e.getClass().getSimpleName());
        logMap.put("error_message", e.getMessage());

        log.error("{}", logMap);
        log.debug("StackTrace: ", e);
    }

    private void logCommonException(
            HttpServletRequest request,
            ErrorCode errorCode
    ) {
        request.setAttribute("errorLoggedByGlobal", true);

        String traceId = (String) request.getAttribute("traceId");

        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("type", "CLIENT_ERROR");
        logMap.put("trace_id", traceId);
        logMap.put("error_code", errorCode.name());
        logMap.put("status", errorCode.getStatus());

        log.info("{}", logMap);
    }
}
