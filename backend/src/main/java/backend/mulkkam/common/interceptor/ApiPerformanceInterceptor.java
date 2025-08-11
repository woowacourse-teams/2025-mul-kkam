package backend.mulkkam.common.interceptor;

import static net.logstash.logback.argument.StructuredArguments.kv;

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
    ) throws Exception {
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
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime == null) {
            return;
        }

        long responseTime = System.currentTimeMillis() - startTime;
        String uri = (String) request.getAttribute(REQUEST_URI_ATTRIBUTE);
        String traceId = (String) request.getAttribute("traceId");

        if (responseTime > RESPONSE_TIME_THRESHOLD) {
            API_PERF.warn("perf",
                    kv("traceId", traceId),
                    kv("method_type", request.getMethod()),
                    kv("uri", uri),
                    kv("response_time", responseTime),
                    kv("status", response.getStatus()),
                    kv("threshold_ms", RESPONSE_TIME_THRESHOLD)
            );
            log.warn("[API Performance]: traceId = {}, {} {} - {}ms [Status: {}]",
                    traceId,
                    request.getMethod(),
                    uri,
                    responseTime,
                    response.getStatus()
            );
            return;
        }
        API_PERF.info("perf",
                kv("traceId", traceId),
                kv("method_type", request.getMethod()),
                kv("uri", uri),
                kv("response_time", responseTime),
                kv("status", response.getStatus())
        );
        log.info("[API Performance]: traceId = {}, {} {} - {}ms [Status: {}]",
                traceId,
                request.getMethod(),
                uri,
                responseTime,
                response.getStatus()
        );
    }
}
