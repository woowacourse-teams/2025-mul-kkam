package backend.mulkkam.common.interceptor;

import backend.mulkkam.common.auth.AuthContext;
import backend.mulkkam.common.auth.annotation.RequireAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }
        RequireAuth requireAuth = method.getMethodAnnotation(RequireAuth.class);
        if (requireAuth == null) {
            return true;
        }
        AuthContext authContext = new AuthContext(request, response);
        return requireAuth.level().authorize(authContext);
    }
}
