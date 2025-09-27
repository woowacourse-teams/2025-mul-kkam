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

    /**
     * 들어오는 HTTP 요청을 가로채어 트레이스 아이디를 설정하고 요청/응답 내용을 선택적으로 로깅한 뒤 체인을 실행하며 응답 바디를 최종 응답으로 복사한다.
     *
     * 처리 흐름:
     * - 요청을 ContentCachingRequestWrapper와 ContentCachingResponseWrapper로 래핑하여 본문을 여러 번 읽을 수 있도록 준비합니다.
     * - MDC에 생성한 traceId를 추가합니다.
     * - 요청 경로가 제외 패턴에 포함되면 로깅을 생략하고 필터 체인만 실행합니다(예외 발생 시 응답 정보는 기록함).
     * - 제외되지 않는 경우 요청 URI와 헤더를 기록하고 필터 체인을 실행한 후, 에러 로깅 플래그가 설정되어 있지 않으면 응답을 기록합니다.
     * - 최종적으로 래핑된 응답의 바디를 실제 응답으로 복사하고 MDC를 정리합니다.
     *
     * @param request     현재 HTTP 요청
     * @param response    현재 HTTP 응답
     * @param filterChain 이어서 실행할 필터 체인
     * @throws ServletException 필터 체인 실행 중 서블릿 처리 오류가 발생한 경우 전달됩니다.
     * @throws IOException      입력/출력 처리 중 오류가 발생한 경우 전달됩니다.
     */
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

    /**
     * 요청 URI가 로그 제외 패턴 목록 중 하나와 일치하는지 판단한다.
     *
     * 주어진 요청의 경로를 EXCLUDE_PATTERNS에 있는 Ant 스타일 패턴과 비교하여 하나라도 매치되면 제외 대상으로 간주한다.
     *
     * @param req 검사할 HTTP 요청
     * @return 일치하는 패턴이 하나라도 있으면 `true`, 그렇지 않으면 `false`
     */
    private boolean isExcluded(HttpServletRequest req) {
        String path = req.getRequestURI();
        return EXCLUDE_PATTERNS.stream()
                .anyMatch(p -> PATH_MATCHER.match(p, path)
                );
    }

    /**
     * 16자 길이의 추적 식별자(traceId)를 생성합니다.
     *
     * UUID를 기반으로 생성된 16자리 16진수 문자열을 반환합니다.
     *
     * @return 16자 길이의 16진수 문자열 형식의 traceId
     */
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
