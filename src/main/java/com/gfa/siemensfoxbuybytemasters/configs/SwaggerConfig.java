package com.gfa.siemensfoxbuybytemasters.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        info = @Info(
                title = "Siemens-FoxBuy-ByteMasters API",
                version = "1.0",
                description = "Siemens-FoxBuy-ByteMasters application documentation"),
        servers = {
                @Server(description = "localhost",
                        url = "http://localhost:8080/"),
                @Server(description = "Production host",
                        url = "https://www.someurl.com/")
        }
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class SwaggerConfig {
}