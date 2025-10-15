package backend.mulkkam.common.filter;

import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<HttpEndpoint> EXCLUDE_ENDPOINTS = List.of(
            /* before signup */
            HttpEndpoint.exact("/auth/kakao", HttpMethod.POST),
            HttpEndpoint.exact("/auth/token/reissue", HttpMethod.POST),
            HttpEndpoint.exact("/nickname/validation", HttpMethod.GET),
            HttpEndpoint.exact("/cups/default", HttpMethod.GET),

            /* swagger */
            HttpEndpoint.prefix("/swagger-ui", HttpMethod.GET),
            HttpEndpoint.prefix("/v3/api-docs", HttpMethod.GET),

            /* etc - for additional functions */
            HttpEndpoint.prefix("/actuator", HttpMethod.GET),
            HttpEndpoint.prefix("/h2-console", HttpMethod.GET, HttpMethod.OPTIONS, HttpMethod.POST),
            HttpEndpoint.prefix("/versions", HttpMethod.GET),

            // TODO: 알림 성능 확인 이후 제거
            HttpEndpoint.prefix("/notifications/test", HttpMethod.GET)
    );

    private final AuthenticationHeaderHandler authenticationHeaderHandler;
    private final OauthJwtTokenHandler oauthJwtTokenHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = authenticationHeaderHandler.extractToken(request);
            Long accountId = oauthJwtTokenHandler.getAccountId(token);
            Long memberId = oauthJwtTokenHandler.getMemberId(token);
            String deviceUuid = oauthJwtTokenHandler.getDeviceUuid(token);
            request.setAttribute("account_id", accountId);
            request.setAttribute("member_id", memberId);
            request.setAttribute("device_uuid", deviceUuid);
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (CommonException e) {
            request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, e.getErrorCode().getStatus().value());
            response.sendError(e.getErrorCode().getStatus().value());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        return EXCLUDE_ENDPOINTS.stream()
                .anyMatch(endpoint -> endpoint.isMatchedWith(requestURI, method));
    }

    private enum MatchType {
        EXACT,
        PREFIX,
        ;
    }

    private record HttpEndpoint(
            String pattern,
            List<HttpMethod> methods,
            MatchType type
    ) {
        static HttpEndpoint exact(String path, HttpMethod... methods) {
            return new HttpEndpoint(path, List.of(methods), MatchType.EXACT);
        }

        static HttpEndpoint prefix(String prefix, HttpMethod... methods) {
            return new HttpEndpoint(prefix, List.of(methods), MatchType.PREFIX);
        }

        boolean isMatchedWith(String uri, String method) {
            if (methods.stream().noneMatch(m -> m.name().equalsIgnoreCase(method))) {
                return false;
            }
            return switch (type) {
                case EXACT -> uri.equals(pattern);
                case PREFIX -> uri.startsWith(pattern);
            };
        }
    }
}
