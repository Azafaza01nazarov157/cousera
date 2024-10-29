package org.example.cursera.config.props;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "application.authentication.jwt", ignoreUnknownFields = false)
public class CorsProp {
    @NotNull
    private String secret;
    @NotNull
    private String base64Secret;
    @NotNull
    private long tokenValidityInSeconds;
    @NotNull
    private long tokenValidityInSecondsForRememberMe;
}
