package backend.mulkkam.common.filter;

import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationHeaderHandler authenticationHeaderHandler;
    private final OauthJwtTokenHandler oauthJwtTokenHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (uri.equals("/members") && method.equalsIgnoreCase("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authenticationHeaderHandler.extractToken(request);
            oauthJwtTokenHandler.getSubject(token);
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) { // TODO: CommonException 변경
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
