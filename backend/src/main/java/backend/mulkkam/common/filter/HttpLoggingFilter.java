package backend.mulkkam.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> EXCLUDE_PATTERNS = List.of(
            "/.git/**",
            "/.env",
            "/favicon.ico",
            "/robots.txt",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );


    @Value("${app.logging.mask-auth:true}")
    private boolean maskAuth;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = generateTraceId();
        MDC.put("traceId", traceId);

        ContentCachingRequestWrapper wrappingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappingResponse = new ContentCachingResponseWrapper(response);

        boolean excluded = isExcluded(request);

        if (excluded) {
            try {
                filterChain.doFilter(wrappingRequest, wrappingResponse);
            } catch (Throwable t) {
                printResponse(request, response, wrappingResponse);
            }
            return;
        }

        printRequestUriAndHeaders(wrappingRequest);
        try {
            filterChain.doFilter(wrappingRequest, wrappingResponse);

            Boolean alreadyErrorLogging = (Boolean) request.getAttribute("errorLoggedByGlobal");
            if (alreadyErrorLogging == null || !alreadyErrorLogging) {

                printResponse(request, response, wrappingResponse);
            }
            wrappingResponse.copyBodyToResponse();
        } finally {
            MDC.clear();
        }
    }

    private boolean isExcluded(HttpServletRequest req) {
        String path = req.getRequestURI();
        return EXCLUDE_PATTERNS.stream()
                .anyMatch(p -> PATH_MATCHER.match(p, path)
                );
    }

    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private void printRequestUriAndHeaders(ContentCachingRequestWrapper request) {
        String methodType = request.getMethod();
        String uri = buildDecodedRequestUri(request);
        String auth = request.getHeader("Authorization");
        if (maskAuth) {
            auth = maskAuthorization(auth);
        }
        log.info("[REQUEST] {} {} token = {}", methodType, uri, auth);
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

    private void printResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            ContentCachingResponseWrapper responseWrapper
    ) {
        Long accountId = (Long) request.getAttribute("account_id");
        String uri = buildDecodedRequestUri(request);
        String auth = response.getHeader("Authorization");
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        if (maskAuth) {
            auth = maskAuthorization(auth);
        }

        String body;
        try {
            body = objectMapper.readTree(responseWrapper.getContentAsByteArray())
                    .toPrettyString()
                    .replaceAll("\\R\\s*\\}$", "}");
            if (body.isEmpty()) {
                body = "NONE";
            }
        } catch (IOException e) {
            body = responseWrapper.getContentType() + "NOT JSON";
        }

        log.info("[RESPONSE] {} accountId = {}, ({}) token = {}, responseBody: {}", uri, accountId, status, auth, body);
    }

    private String buildDecodedRequestUri(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = decodeQuery(request.getQueryString());
        return (query == null || query.isBlank()) ? path : path + "?" + query;
    }
}
