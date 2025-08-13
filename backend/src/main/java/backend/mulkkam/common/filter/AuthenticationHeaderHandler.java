package backend.mulkkam.common.filter;

import backend.mulkkam.common.exception.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import static backend.mulkkam.common.exception.errorCode.UnauthorizedErrorCode.UNAUTHORIZED;

@Component
public class AuthenticationHeaderHandler {

    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    public String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            throw new CommonException(UNAUTHORIZED);
        }

        if (!authorization.startsWith(AUTHORIZATION_PREFIX)) {
            throw new CommonException(UNAUTHORIZED);
        }

        return authorization.substring(AUTHORIZATION_PREFIX.length());
    }
}
