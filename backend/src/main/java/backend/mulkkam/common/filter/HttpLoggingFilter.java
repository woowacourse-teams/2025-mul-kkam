package backend.mulkkam.common.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Value("${app.logging.mask-auth:true}")
    private boolean maskAuth;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException, IOException {
        String traceId = generateTraceId();
        request.setAttribute("traceId", traceId);
        ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);
        printRequestUriAndHeaders(wrappingRequest);

        filterChain.doFilter(wrappingRequest, wrappingResponse);
        Boolean alreadyErrorLogging = (Boolean) request.getAttribute("errorLoggedByGlobal");
        if (alreadyErrorLogging == null || !alreadyErrorLogging) {
            printResponseHeader(traceId, response);
            printResponseBody(wrappingResponse);
        }
        wrappingResponse.copyBodyToResponse();
    }

    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private void printRequestUriAndHeaders(ContentCachingRequestWrapper request) {
        String traceId = (String) request.getAttribute("traceId");
        String methodType = request.getMethod();
        String uri = buildDecodedRequestUri(request);
        String auth = request.getHeader("Authorization");
        if (maskAuth) {
            auth = maskAuthorization(auth);
        }

        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("type", "REQUEST");
        logMap.put("trace_id", traceId);
        logMap.put("method_type", methodType);
        logMap.put("uri", uri);
        logMap.put("auth", auth);

        log.info("{}", logMap);
    }

    private String buildDecodedRequestUri(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = decodeQuery(request.getQueryString());
        return (query == null || query.isBlank()) ? path : path + "?" + query;
    }

    private String decodeQuery(String rawQuery) {
        if (rawQuery == null) {
            return null;
        }
        try {
            return URLDecoder.decode(rawQuery, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return rawQuery;
        }
    }

    private String maskAuthorization(String raw) {
        if (raw == null || raw.isBlank()) {
            return "null";
        }
        String bearer = "Bearer ";
        if (raw.regionMatches(true, 0, bearer, 0, bearer.length())) {
            String token = raw.substring(bearer.length());
            return bearer + abbreviateToken(token);
        }
        return "****";
    }

    private String abbreviateToken(String t) {
        if (t == null || t.isEmpty()) {
            return "null";
        }
        if (t.length() <= 10) {
            return t.charAt(0) + "****";
        }
        return t.substring(0, 6) + "..." + t.substring(t.length() - 4);
    }

    private void printResponseHeader(
            String traceId,
            HttpServletResponse response
    ) {
        String auth = response.getHeader("Authorization");
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        if (maskAuth) {
            auth = maskAuthorization(auth);
        }

        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("type", "RESPONSE");
        logMap.put("trace_id", traceId);
        logMap.put("status", status.value());
        logMap.put("auth", auth);

        try {
            log.info("{}", objectMapper.writeValueAsString(logMap));
        } catch (Exception e) {
            log.warn("Failed to serialize logMap to JSON", e);
            log.info("{}", logMap);
        }
    }

    private void printResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] bytes = responseWrapper.getContentAsByteArray();

        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("type", "RESPONSE_BODY");

        try {
            if (bytes.length == 0) {
                logMap.put("response_body", "NONE");
            } else {
                JsonNode node = objectMapper.readTree(bytes);
                logMap.put("response_body", node);
            }
        } catch (IOException e) {
            String contentType = responseWrapper.getContentType();
            logMap.put("non_json", true);
            logMap.put("content_type", contentType == null ? "unknown" : contentType);
            logMap.put("body_text", new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
        }

        try {
            log.info("{}", objectMapper.writeValueAsString(logMap));
        } catch (Exception ex) {
            log.warn("Failed to serialize logMap to JSON", ex);
            log.info("{}", logMap);
        }
    }
}
