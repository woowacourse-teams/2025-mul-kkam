package backend.mulkkam.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiPerformanceInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String REQUEST_URI_ATTRIBUTE = "requestUri";
    private static final int RESPONSE_TIME_THRESHOLD = 3_000;

    private final ObjectMapper objectMapper;

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
            Exception ex
    ) throws Exception {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        String requestUri = (String) request.getAttribute(REQUEST_URI_ATTRIBUTE);

        if (startTime != null) {
            long responseTime = System.currentTimeMillis() - startTime;

            Map<String, Object> logMap = new LinkedHashMap<>();
            logMap.put("type", "API_Performance");
            logMap.put("method_type", request.getMethod());
            logMap.put("uri", requestUri);
            logMap.put("response_time", responseTime);
            logMap.put("status", response.getStatus());

            try {
                String jsonLog = objectMapper.writeValueAsString(logMap);
                if (responseTime > RESPONSE_TIME_THRESHOLD) {
                    log.warn("{}", jsonLog);
                } else {
                    log.info("{}", jsonLog);
                }
            } catch (Exception e) {
                log.warn("Failed to serialize logMap to JSON", e);
                if (responseTime > RESPONSE_TIME_THRESHOLD) {
                    log.warn("{}", logMap);
                } else {
                    log.info("{}", logMap);
                }
            }
        }
    }
}
