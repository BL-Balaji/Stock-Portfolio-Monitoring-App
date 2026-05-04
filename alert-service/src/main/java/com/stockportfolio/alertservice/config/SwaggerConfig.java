package com.stockportfolio.alertservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 3 configuration for Alert Service.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI alertServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Alert Service API")
                        .description("Stock Portfolio Monitoring App — Alert Service\n\n" +
                                "Manages configurable stock price and portfolio loss alerts.\n\n" +
                                "**Alert Types**:\n" +
                                "- `PRICE_THRESHOLD` — triggers when stock price goes ABOVE or BELOW a set value\n" +
                                "- `PORTFOLIO_LOSS` — triggers when total portfolio loss exceeds a percentage\n\n" +
                                "**Evaluation**: Alerts are evaluated every 5 minutes via scheduled job.\n\n" +
                                "**Authentication**: Pass `X-User-Id` header for all requests.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nivrutti")
                                .url("https://github.com/BL-Nivrutti")))
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
