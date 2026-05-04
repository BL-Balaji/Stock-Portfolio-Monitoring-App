package com.stockportfolio.pricefetcher.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 3 configuration for Price Fetcher Service.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI priceFetcherServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Price Fetcher Service API")
                        .description("Stock Portfolio Monitoring App — Price Fetcher Service\n\n" +
                                "Fetches real-time stock prices from TwelveData API (mock mode available), " +
                                "caches results, and provides gain/loss calculations.\n\n" +
                                "**Mock mode**: Set `external.api.use-mock=true` in config to use simulated prices.\n\n" +
                                "**Scheduled refresh**: Prices auto-refresh every 5 minutes.")
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
