package backend.mulkkam.common.config;

import backend.mulkkam.common.filter.HttpLoggingFilter;
import backend.mulkkam.common.filter.JwtAuthenticationFilter;
import backend.mulkkam.common.interceptor.ApiPerformanceInterceptor;
import backend.mulkkam.common.interceptor.AuthorizationInterceptor;
import backend.mulkkam.common.resolver.MemberAndDeviceUuidResolver;
import backend.mulkkam.common.resolver.MemberResolver;
import backend.mulkkam.common.resolver.OauthAccountResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
    private final AuthorizationInterceptor authorizationInterceptor;

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
        registry.addInterceptor(authorizationInterceptor)
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>(new CorsFilter(source));
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // 가장 먼저 실행
        return filterBean;
    }
}
