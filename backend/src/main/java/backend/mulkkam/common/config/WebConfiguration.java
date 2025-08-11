package backend.mulkkam.common.config;

import backend.mulkkam.common.filter.ApiPerformanceInterceptor;
import backend.mulkkam.common.filter.HttpLoggingFilter;
import backend.mulkkam.common.filter.JwtAuthenticationFilter;
import backend.mulkkam.common.resolver.MemberResolver;
import backend.mulkkam.common.resolver.OauthAccountResolver;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
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
    private final ApiPerformanceInterceptor apiPerformanceInterceptor;
    private final HttpLoggingFilter httpLoggingFilter;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://mulkkam.stoplight.io")
                .allowedMethods("*")
                .allowCredentials(true);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(oauthAccountResolver);
        resolvers.add(memberResolver);
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

    @Bean
    ApplicationRunner probe(MeterRegistry r) {
        return args -> System.out.println("MeterRegistry=" + r.getClass().getName());
    }
}
