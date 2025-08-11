package backend.mulkkam.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI mulkkamOpenAPI() {
        String title = "Mul-Kkam Application Swagger";
        String description = "물깜 서비스의 API 문서입니다.";

        Info info = new Info().title(title).description(description).version("1.0.0");

        String schemeName = "Authorization";
        SecurityScheme securityScheme = new SecurityScheme()
                .name(schemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(schemeName);

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .schemaRequirement(schemeName, securityScheme);
    }
}
