package com.venvas.pocamarket.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // http://localhost:8080/swagger-ui/index.html
        // http://localhost:8080/v3/api-docs
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("PocaMarket Swagger")
                .description("PocaMarket 유저 및 인증, REST API 호출")
                .version("1.0.0");
    }
}
