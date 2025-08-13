package backend.mulkkam.common.filter;

import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<HttpEndpoint> EXCLUDE_ENDPOINTS = List.of(
            HttpEndpoint.of("/auth", HttpMethod.POST),
            HttpEndpoint.of("/swagger-ui", HttpMethod.GET),
            HttpEndpoint.of("/v3/api-docs", HttpMethod.GET),
            HttpEndpoint.of("/nickname/validation", HttpMethod.GET),
            HttpEndpoint.of("/actuator", HttpMethod.GET),
            HttpEndpoint.of("/h2-console", HttpMethod.GET, HttpMethod.OPTIONS, HttpMethod.POST)
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
            Long subject = oauthJwtTokenHandler.getSubject(token);
            request.setAttribute("subject", subject);
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) { // TODO: CommonException 변경
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        return EXCLUDE_ENDPOINTS.stream()
                .anyMatch(endpoint -> endpoint.isMatchedWith(requestURI, method));
    }

    private record HttpEndpoint(
            String pathPrefix,
            List<HttpMethod> methods
    ) {
        public static HttpEndpoint of(String pathPrefix, HttpMethod... methods) {
            return new HttpEndpoint(pathPrefix, List.of(methods));
        }

        public boolean isMatchedWith(String uri, String method) {
            return uri.startsWith(pathPrefix)
                    && methods.stream().anyMatch(target -> target.name().equalsIgnoreCase(method));
        }
    }
}
