package com.venvas.pocamarket.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public OpenAPI openAPI() {
        // http://localhost:8080/swagger-ui/index.html
        // http://localhost:8080/v3/api-docs
        OpenAPI openAPI = new OpenAPI()
                .components(new Components())
                .info(apiInfo());

        // 환경별 서버 설정
        if ("prod".equals(activeProfile)) {
            openAPI.servers(List.of(
                    new Server().url("https://pocamarket.co.kr/api").description("Production Server"),
                    new Server().url("https://www.pocamarket.co.kr/api").description("Production Server (www)")
            ));
        } else {
            openAPI.servers(List.of(
                    new Server().url("http://localhost:8080").description("Local Development Server")
            ));
        }

        return openAPI;
    }

    private Info apiInfo() {
        return new Info()
                .title("PocaMarket Swagger")
                .description("PocaMarket 유저 및 인증, REST API 호출")
                .version("1.0.0");
    }
}
