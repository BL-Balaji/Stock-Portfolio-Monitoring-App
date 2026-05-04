package com.stockportfolio.portfolioservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 3 configuration for Portfolio Service.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI portfolioServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio Service API")
                        .description("Stock Portfolio Monitoring App — Portfolio Service\n\n" +
                                "Manages user portfolios and stock holdings with real-time gain/loss calculation.\n\n" +
                                "**Authentication**: Pass `X-User-Id` header (user ID from JWT) for all requests.\n\n" +
                                "When running via API Gateway, the gateway injects `X-User-Id` automatically from the JWT token.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Balaji")
                                .url("https://github.com/BL-Balaji")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token from User Service /api/auth/login")));
    }
}
