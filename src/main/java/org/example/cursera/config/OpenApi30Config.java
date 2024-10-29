package org.example.cursera.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "COURSERA API", version = "v1"),
        servers = {@Server(url = "/", description = "Default Server URL")})
@SecuritySchemes(
        {
                @SecurityScheme(name = OpenApi30Config.BEARER_AUTH, type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer"),
        }
)
public class OpenApi30Config {
        public static final String BEARER_AUTH = HttpHeaders.AUTHORIZATION;
}
