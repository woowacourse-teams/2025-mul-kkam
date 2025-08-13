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
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiPerformanceInterceptor implements HandlerInterceptor {

    private static final String ROUTE_PATTERN_ATTRIBUTE = "routePattern";
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

        Object bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (bestMatchingPattern instanceof String s) {
            request.setAttribute(ROUTE_PATTERN_ATTRIBUTE, s);
        }

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

        String originalUri = extractOriginalUri(request);
        String logUri = normalizeRoutePath(request, originalUri);
        Long oauthId = (Long) request.getAttribute("oauth_id");

        if (responseTime > RESPONSE_TIME_THRESHOLD) {
            API_PERF.warn("perf",
                    kv("type", "API_Performance"),
                    kv("method_type", request.getMethod()),
                    kv("uri", logUri),
                    kv("response_time", responseTime),
                    kv("status", response.getStatus())
            );
            log.warn("[API Performance] oauthId = {}, {} {} - {}ms [Status: {}]",
                    oauthId,
                    request.getMethod(),
                    originalUri,
                    responseTime,
                    response.getStatus()
            );
            return;
        }
        API_PERF.info("perf",
                kv("type", "API_Performance"),
                kv("method_type", request.getMethod()),
                kv("uri", logUri),
                kv("response_time", responseTime),
                kv("status", response.getStatus())
        );
        log.info("[API Performance]: oauthId = {}, {} {} - {}ms [Status: {}]",
                oauthId,
                request.getMethod(),
                originalUri,
                responseTime,
                response.getStatus()
        );
    }

    private String extractOriginalUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (uri == null) uri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        if (uri == null) uri = request.getRequestURI();
        return uri;
    }

    private String normalizeRoutePath(HttpServletRequest request, String originalUri) {
        String route = (String) request.getAttribute(ROUTE_PATTERN_ATTRIBUTE);
        if (route == null) {
            Object best = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            route = (best instanceof String s) ? s : originalUri;
        }

        StringBuilder sb = new StringBuilder();
        for (String seg : route.split("/")) {
            if (seg.isEmpty()) continue;
            if (seg.startsWith("{") && seg.endsWith("}")) continue;
            sb.append('/').append(seg);
        }

        if (sb.isEmpty()) {
            return "/";
        }
        return sb.toString();
    }
}
