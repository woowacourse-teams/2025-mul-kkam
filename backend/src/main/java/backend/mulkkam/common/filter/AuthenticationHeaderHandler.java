package backend.mulkkam.common.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHeaderHandler {

    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    public String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            // TODO: CommonException 으로 변경
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
        }

        if (!authorization.startsWith(AUTHORIZATION_PREFIX)) {
            // TODO: CommonException 으로 변경
            throw new IllegalArgumentException("유효하지 않은 인증 헤더입니다.");
        }

        return authorization.substring(AUTHORIZATION_PREFIX.length());
    }
}
