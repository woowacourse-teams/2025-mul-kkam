package backend.mulkkam.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI mulkkamOpenAPI() {
        String title = "Mul-Kkam Application Swagger";
        String description = "물깜 서비스의 API 문서입니다.";

        Info info = new Info().title(title).description(description).version("1.0.0");

        return new OpenAPI().info(info);
    }
}
