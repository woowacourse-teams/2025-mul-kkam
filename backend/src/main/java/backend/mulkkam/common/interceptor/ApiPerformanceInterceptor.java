package backend.mulkkam.common.interceptor;

import static net.logstash.logback.argument.StructuredArguments.kv;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiPerformanceInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String REQUEST_URI_ATTRIBUTE = "requestUri";
    private static final int RESPONSE_TIME_THRESHOLD = 3_000;
    private static final Logger API_PERF = LoggerFactory.getLogger("API_PERF");

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ){
        long startTime = System.currentTimeMillis();

        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        request.setAttribute(REQUEST_URI_ATTRIBUTE, request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception
    ) {
        String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (uri == null) uri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        if (uri == null) uri = request.getRequestURI();

        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime == null) {
            return;
        }

        long responseTime = System.currentTimeMillis() - startTime;

        if (responseTime > RESPONSE_TIME_THRESHOLD) {
            API_PERF.warn("perf",
                    kv("type", "API_Performance"),
                    kv("method_type", request.getMethod()),
                    kv("uri", uri),
                    kv("response_time", responseTime),
                    kv("status", response.getStatus())
            );
            log.warn("[API Performance]: {} {} - {}ms [Status: {}]",
                    request.getMethod(),
                    uri,
                    responseTime,
                    response.getStatus()
            );
            return;
        }
        API_PERF.info("perf",
                kv("type", "API_Performance"),
                kv("method_type", request.getMethod()),
                kv("uri", uri),
                kv("response_time", responseTime),
                kv("status", response.getStatus())
        );
        log.info("[API Performance]: {} {} - {}ms [Status: {}]",
                request.getMethod(),
                uri,
                responseTime,
                response.getStatus()
        );
    }
}
