package backend.mulkkam.common.config;

import backend.mulkkam.common.filter.HttpLoggingFilter;
import backend.mulkkam.common.filter.JwtAuthenticationFilter;
import backend.mulkkam.common.interceptor.ApiPerformanceInterceptor;
import backend.mulkkam.common.resolver.MemberAndDeviceUuidResolver;
import backend.mulkkam.common.resolver.MemberResolver;
import backend.mulkkam.common.resolver.OauthAccountResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OauthAccountResolver oauthAccountResolver;
    private final MemberResolver memberResolver;
    private final MemberAndDeviceUuidResolver memberAndDeviceUuidResolver;
    private final ApiPerformanceInterceptor apiPerformanceInterceptor;
    private final HttpLoggingFilter httpLoggingFilter;

    /**
     * 컨트롤러 핸들러 메서드의 인자 변환을 위해 커스텀 HandlerMethodArgumentResolver들을 등록한다.
     *
     * 등록되는 리졸버들은 OAuth 계정 해석기, 멤버 해석기, 멤버와 디바이스 UUID를 함께 해석하는 해석기 순서로 추가된다.
     *
     * @param resolvers 리졸버를 추가할 리스트; 메서드 실행 후 커스텀 리졸버들이 이 리스트에 추가된다.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(oauthAccountResolver);
        resolvers.add(memberResolver);
        resolvers.add(memberAndDeviceUuidResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiPerformanceInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(jwtAuthenticationFilter);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    public FilterRegistrationBean<HttpLoggingFilter> customHttpLoggingFilter() {
        FilterRegistrationBean<HttpLoggingFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(httpLoggingFilter);
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterBean;
    }
}
