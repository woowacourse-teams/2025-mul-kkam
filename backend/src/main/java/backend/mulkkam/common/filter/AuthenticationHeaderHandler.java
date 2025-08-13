package backend.mulkkam.common.filter;

import static backend.mulkkam.common.exception.errorCode.UnauthorizedErrorCode.INVALID_AUTHORIZATION_HEADER;
import static backend.mulkkam.common.exception.errorCode.UnauthorizedErrorCode.MISSING_AUTHORIZATION_HEADER;

import backend.mulkkam.common.exception.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHeaderHandler {

    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    public String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            throw new CommonException(MISSING_AUTHORIZATION_HEADER);
        }

        if (!authorization.startsWith(AUTHORIZATION_PREFIX)) {
            throw new CommonException(INVALID_AUTHORIZATION_HEADER);
        }

        return authorization.substring(AUTHORIZATION_PREFIX.length());
    }
}
